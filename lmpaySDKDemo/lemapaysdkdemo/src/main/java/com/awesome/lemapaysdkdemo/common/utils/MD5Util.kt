package com.awesome.lemapaysdkdemo.common.utils

import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.text.TextUtils
import okhttp3.internal.and
import java.io.UnsupportedEncodingException
import java.lang.RuntimeException


/**
 * @Description:
 *
 */

object MD5Util {



    fun md5(string: String): String {
        if (TextUtils.isEmpty(string)) {
            return ""
        }
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(string.toByteArray())
            var result = ""
            for (b in bytes) {
                var temp = Integer.toHexString(b and 0xff)
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result += temp
            }
            return result.toUpperCase()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

}