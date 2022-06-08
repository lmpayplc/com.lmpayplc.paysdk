package com.awesome.lemapaysdkdemo.moudles.main


import Decoder.BASE64Encoder
import android.content.Context

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity

import com.awesome.lemapay.demo.R
import com.awesome.lemapaysdkdemo.api.ApiServices
import com.awesome.lemapaysdkdemo.common.utils.OrderIdGenerateUtil
import com.awesome.lemapaysdkdemo.common.utils.SignUtil
import com.awesome.lemapaysdkdemo.moudles.app.App

import com.awesomeglobal.paysdk.auth.model.SendAuth
import com.awesomeglobal.paysdk.modelpay.PayFrozenReq
import com.awesomeglobal.paysdk.modelpay.PayLebeiReq
import com.awesomeglobal.paysdk.modelpay.PayReq
import com.awesomeglobal.paysdk.modelpay.PaySignReq
//import com.awesomeglobal.paysdk.utils.Log
import com.google.android.material.switchmaterial.SwitchMaterial
import io.reactivex.Observable

import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function

import io.reactivex.schedulers.Schedulers
import java.util.*


class MainActivity : AppCompatActivity() {


    private val switchMaterial by lazy { findViewById<SwitchMaterial>(R.id.switch_material) }
    private val paySdkBtn by lazy { findViewById<Button>(R.id.pay_btn_sdk) }
    private val customAmountSwitchMaterial by lazy { findViewById<SwitchMaterial>(R.id.custom_amount_switch_material) }
    private val customAmountEt by lazy { findViewById<EditText>(R.id.custom_amount_et) }


    private val signWithholdBtn by lazy { findViewById<Button>(R.id.sign_withhold_btn) }//签约代扣


    private val preAuthBtn by lazy { findViewById<Button>(R.id.pre_auth_btn) }
    private val withdrawApplyBtn by lazy { findViewById<Button>(R.id.withdraw_apply_btn) }//提现申请

    private val loginBtn by lazy { findViewById<Button>(R.id.login_btn) }
    private val resultTv by lazy { findViewById<TextView>(R.id.result_tv) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchMaterial.isChecked = isH5()
        switchMaterial.setOnCheckedChangeListener { buttonView, isChecked ->
            saveBoo(isChecked)
        }
        paySdkBtn.setOnClickListener {
            var amount = customAmountEt.text.toString()
            if (customAmountSwitchMaterial.isChecked) {
                if (TextUtils.isEmpty(amount) || amount.toInt() == 0) {
                    Toast.makeText(this, "请检查自定义支付金额是否大于等于0.01", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            } else {
                amount = "0.01"
            }

            this.pay(mustH5 = isH5(), amount = amount)
        }
        customAmountSwitchMaterial.setOnCheckedChangeListener { _, isChecked ->
            customAmountEt.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                paySdkBtn.text = "支付 0.01 USD"
                customAmountEt.setText("")
            }
        }
        customAmountEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                paySdkBtn.text = "支付 ${if (s.toString().isEmpty()) "0.01" else s.toString()} USD"
            }

        })

        signWithholdBtn.setOnClickListener {//签约代扣
            val sendAuth = PaySignReq()
            sendAuth.openId = App.appId
            sendAuth.sign_data = createPaySignReq()
            sendAuth.mustH5 = isH5()
            //scope = "pay_sign" 签约免密支付
            sendAuth.isTest = 1//2-内⽹ 1-测试 3-⽣产
            App.lmAuth.sendReq(sendAuth)
        }
        preAuthBtn.setOnClickListener {//预授权
        }
        withdrawApplyBtn.setOnClickListener { }
        loginBtn.setOnClickListener(this::signByLMPay)


    }

    private fun createPaySignReq(): String {
        val param = TreeMap<String, String>()
        param["app_id"] = App.appId
        param["mach_id"] = App.machId
        param["nonce_str"] = OrderIdGenerateUtil.getRandomString(32)//从服务器获取
        param["co_sign_id"] = OrderIdGenerateUtil.getRandomString(32)//从服务器获取

        param["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        param["sign_type"] = "MD5"
        param["sign"] = SignUtil.getSign(param, App.appkey)

        val toJson = App.gson.toJson(param)
        val res = BASE64Encoder().encode(toJson.toByteArray()).replace("\n", "")
        return res
    }


    private fun signByLMPay(v: View) {
        val sendAuth = SendAuth.Req()
        //scope = "login" 为授权登录
        sendAuth.scope = "login"
        sendAuth.mustH5 = isH5()
        sendAuth.state = "第三⽅⽤户⾃定义的值，可不填写"
        sendAuth.isTest = 1//2-内⽹ 1-测试 3-⽣产
        App.lmAuth.sendReq(sendAuth)
    }


    private fun pay(mustH5: Boolean = false, amount: String) {
        val subscribe = Observable.create(ObservableOnSubscribe<String> {
            val param = createServiceOrder(amount)
            val createOrder = ApiServices.createOrder(param)
            if (createOrder.isSuccess()) {
                it.onNext(createOrder.data.transaction_id)
                it.onComplete()
            } else {
                it.onError(Throwable(createOrder.msg))
            }
        }).flatMap { t ->
            val req = PayReq()
            req.openId = App.appId
            req.language = "zh-cn"
            req.mustH5 = mustH5
            req.isTest = 1// 2-测试 3-⽣产
            req.sign_data = createSdkSign(t)
            val sendReq = App.lmAuth.sendReq(req)
            Observable.just(sendReq)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    //TODO  处理成功
                },
                {
                    Toast.makeText(this@MainActivity, R.string.pay_failed, Toast.LENGTH_SHORT)
                        .show()
                    resultTv.text = it.message
                }
            )
    }


    /**
     * 构建订单信息，该步骤应由服务器完成
     *
     * Build order information,This step should be done by the server
     */
    private fun createServiceOrder(amount: String = "0.01"): TreeMap<String, String> {
        val param = TreeMap<String, String>()
        param["appid"] = App.appId
        param["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        param["mch_id"] = App.machId
        param["nonce_str"] = OrderIdGenerateUtil.getRandomString(32)
        param["fee_type"] = "USD"
        param["total_fee"] = amount
        param["body"] = "General information"
        param["out_trade_no"] = OrderIdGenerateUtil.getRandomString()
        param["return_pay_code"] = "0"
        param["platform_subsidy"] = "0"
        param["sign_type"] = "MD5"
        param["sign"] = SignUtil.getSign(param, App.appkey)

        return param
    }

    /**
     * 该支付数据由LMPay服务器生成返回。
     * The payment data is generated and returned by the LM Pay server
     * {
    "app_id" : "2003101655018656525",
    "order_id" : "2108191738497089407",
    "nonce_str": "84970891629368447479",
    "timestamp": 1629368447479,
    "sign": "58EE70B86482C199FC65D47876879E95"
    }
     */
    private fun createSdkSign(order_id: String): String {
        val param = TreeMap<String, String>()
        param["app_id"] = App.appId
        param["order_id"] = order_id
        param["nonce_str"] = OrderIdGenerateUtil.getRandomString(32)
        param["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        param["sign_type"] = "MD5"
        param["sign"] = SignUtil.getSign(param, App.appkey)
        val toJson = App.gson.toJson(param)
        val res = BASE64Encoder().encode(toJson.toByteArray()).replace("\n", "")
        return res
    }

    private fun saveBoo(boolean: Boolean) {
        val sp = getSharedPreferences("name", Context.MODE_PRIVATE)
        sp.edit().putBoolean("isH5", boolean).apply()
    }

    private fun isH5(): Boolean {
        val sp = getSharedPreferences("name", Context.MODE_PRIVATE)
        return sp.getBoolean("isH5", false)
    }

}




