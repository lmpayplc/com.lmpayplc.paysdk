package com.awesome.lemapaysdkdemo.moudles.app

import android.app.Application
import com.awesomeglobal.paysdk.openapi.LMAPIFactory
import com.awesomeglobal.paysdk.openapi.LMAuth
//import com.awesomeglobal.paysdk.utils.Log
import com.google.gson.Gson

//import com.squareup.leakcanary.LeakCanary


class App : Application() {
    //    app_id: 2003101655018656525 mechant_id: 21000000103005 secret: e169fa48a5553ff41ddc611faa8895bb
    companion object {
        val appId = "2003101655018656525"
        val appkey = "e169fa48a5553ff41ddc611faa8895bb"
        val machId = "21000000103005"
        lateinit var lmAuth: LMAuth
        lateinit var INSTANCE: App
        val gson = Gson()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        lmAuth = LMAPIFactory.createLMAPI(this, appId)
        lmAuth.registerApp(appId)
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this)

    }
}