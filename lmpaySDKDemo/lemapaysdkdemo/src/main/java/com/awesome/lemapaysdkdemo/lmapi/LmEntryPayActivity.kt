package com.awesome.lemapaysdkdemo.lmapi

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.awesome.lemapay.demo.R
import com.awesome.lemapaysdkdemo.moudles.app.App
import com.awesomeglobal.paysdk.modelbase.BaseReq
import com.awesomeglobal.paysdk.modelbase.BaseResp
import com.awesomeglobal.paysdk.openapi.ILMAPIEventHandler


class LmEntryPayActivity : Activity(), ILMAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.lmAuth.handleIntent(intent, this)
    }

    override fun onReq(baseReq: BaseReq?) {
//        Log.i("baseReq", "baseReq = ${App.gson.toJson(baseReq)}")
    }

    override fun onResp(baseResp: BaseResp) {
        if (baseResp.errCode == 0) {//
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, baseResp.errStr, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}