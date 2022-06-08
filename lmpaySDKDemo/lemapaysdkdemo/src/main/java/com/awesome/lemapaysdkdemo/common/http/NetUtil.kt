package com.awesome.lemapaysdkdemo.common.http

import android.annotation.SuppressLint
import com.awesome.lemapaysdkdemo.moudles.app.App
import com.awesome.lemapaysdkdemo.common.utils.FileUtils
import com.laoshan.pospay.common.http.CustomInterceptor


import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetUtil {
    private const val MAX_CACHE_SIZE = 200 * 1024 * 1024
    private const val TIME = 10
    var httpClient: OkHttpClient

    init {
        val certificatePinner: CertificatePinner = CertificatePinner.Builder()
            .add(
                "clientapi.lmpayplc.com",
                "sha256/E64087D94ojuW6jQDbPVLGFg8FUcXgzb2mEo45hjAWA="
            ).build()
        httpClient = OkHttpClient.Builder()
            .connectTimeout(TIME.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIME.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIME.toLong(), TimeUnit.SECONDS)
            .sslSocketFactory(createSSLSocketFactory(), MyTrustManager())
            .cache(Cache(FileUtils.getHttpCacheDir(App.INSTANCE), MAX_CACHE_SIZE.toLong()))
            .addInterceptor(CustomInterceptor)
            .certificatePinner(certificatePinner)
            .build()


    }

    lateinit var ssfFactory: SSLSocketFactory
    private fun createSSLSocketFactory(): SSLSocketFactory {
        try {
            val mMyTrustManager = MyTrustManager()
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<TrustManager>(mMyTrustManager), SecureRandom())
            ssfFactory = sc.socketFactory
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
        return ssfFactory
    }


    //实现X509TrustManager接口
    class MyTrustManager : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

}

///**
// * 用于包装异步网络请求
// */
//@DelicateCoroutinesApi
//inline fun <T> GlobalScope.fetchData(
//    crossinline func: suspend GlobalScope.() -> T,
//    crossinline resultFunc: (T) -> Unit = {},
//    crossinline errFunc: (Exception) -> Unit = {}
//) {
//    launch(Dispatchers.IO) {
//        try {
//            val result = withContext(Dispatchers.IO) { func() }
//            withContext(Dispatchers.Main) {
//                resultFunc(result)
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                errFunc(e)
//            }
//        }
//    }
//}
