package com.awesome.lemapaysdkdemo.lmapi

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.awesome.lemapaysdkdemo.moudles.app.App
import com.awesome.lemapay.demo.R
import com.awesomeglobal.paysdk.modelbase.BaseReq
import com.awesomeglobal.paysdk.modelbase.BaseResp
import com.awesomeglobal.paysdk.openapi.ILMAPIEventHandler

class LmEntryActivity : Activity(), ILMAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lmentry)
        App.lmAuth.handleIntent(intent, this)
    }

    override fun onReq(p0: BaseReq?) {

    }

    override fun onResp(baseResp: BaseResp) {
        if (baseResp.errCode == 0) {
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, baseResp.errStr, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}