package com.laoshan.pospay.common.http

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.StandardCharsets

object CustomInterceptor : Interceptor {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()
        if (!bodyEncoded(response.headers)) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer
            if (!isPlainText(buffer)) {
                return response
            }
            if (contentLength != 0L) {
                val result = buffer.clone().readString(StandardCharsets.UTF_8)
                val url = response.request.url
                val url1 = url.toUrl()
                val code = response.code
                if (code == 401) {//说明当前token已经过期，需要提示用户进行登陆操作
                    //清理登陆数据

                }
                Log.i("code", "code == $code")
                Log.i("code", "url1 == $url1")
                // todo 拦截器
//                if (DomainConfig.DERBIT_CONTRACT_URL.contains(url1.host)) { //如果是合约调用的地址
//                    if (code == 503) {
//                        EventBus.getDefault().post(DataTagEvent(DataTagEvent.SERVICE_UPGRADE))
//                    }
//                } else {
//
//                    if (code == 401) {
//                        toLoginPage(url1.toString())
//                    } else {
//                        toFrequentlyPage(result)
//                    }
//                }
            }
        }
        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    fun isPlainText(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private val UTF8 = StandardCharsets.UTF_8
}