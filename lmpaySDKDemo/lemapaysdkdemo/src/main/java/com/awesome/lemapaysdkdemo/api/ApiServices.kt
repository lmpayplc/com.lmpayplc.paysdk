package com.awesome.lemapaysdkdemo.api

import com.awesome.lemapaysdkdemo.moudles.app.App
import com.google.gson.reflect.TypeToken
import com.awesome.lemapaysdkdemo.common.http.NetUtil
//import com.awesomeglobal.paysdk.utils.Log
import com.laoshan.pospay.common.param.ParamGenerator
import okhttp3.MultipartBody
import okhttp3.Request
import java.util.*
import kotlin.collections.HashMap

object ApiServices {
    private fun createBuilder(): Request.Builder {
        return Request.Builder()
                .addHeader("UserSource", "android")
                .addHeader("accept-language", "zh_cn")
                .addHeader("lang", "zh_cn")
                .addHeader("Content-Type", "application/json")
    }

    private fun Int.errorCodeExplain(): String {
        return when (this) {
            200 -> "request success"
            404 -> "NOT FOUND"
            502 -> "Service error"
            else -> "other error"
        }
    }

    /**
     * 测试服务器url
     * Test server url
     */
    private const val BaseUrl = "https://testpayapi.lmpayplc.cc/"
    private fun fetchData(
            isGet: Boolean = false,
            param: Map<String, String> = HashMap(),
            requestUrl: String = BaseUrl
    ): String {
        val type = MultipartBody.Builder().setType(MultipartBody.FORM)
        for ((k, v) in param) {
            type.addFormDataPart(k, v)
//            Log.i("value", " k == $k   v == $v")
        }
        val builder = createBuilder()
        val build = if (isGet) {
            builder.get().url(requestUrl).build()
        } else {
            builder.post(type.build()).url(requestUrl).build()
        }
        val call = NetUtil.httpClient.newCall(build)
        val execute = call.execute()

        return if (execute.code == 200) {
            execute.body!!.string()
        } else {
            ParamGenerator.createBuilder()
                    .add("code", execute.code)
                    .add("msg", execute.code.errorCodeExplain() + ",ServiceInfo=${execute.message}")
                    .build()
        }
    }

    /**
     * 订单信息发送给LMPay服务器,并返回transaction_id字段信息
     *
     * The order information is sent to the LM Pay server, and the transaction_id field information is returned
     */
    fun createOrder(param: TreeMap<String, String>): BaseResponse<OrderDetail> {
        val fetchData = fetchData(param = param, requestUrl = "${BaseUrl}legacy/create_order")
        return App.gson.fromJson(
                fetchData,
                object : TypeToken<BaseResponse<OrderDetail>>() {}.type
        )
    }


    fun checkCode(param: TreeMap<String, String>): BaseResponse<OrderDetail> {
        val fetchData = fetchData(param = param, requestUrl = "${BaseUrl}merchant/pos/check_code")

        return App.gson.fromJson(
                fetchData,
                object : TypeToken<BaseResponse<OrderDetail>>() {}.type
        )
    }


}