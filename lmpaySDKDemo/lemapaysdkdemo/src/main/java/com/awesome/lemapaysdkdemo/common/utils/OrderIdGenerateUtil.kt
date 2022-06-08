package com.awesome.lemapaysdkdemo.common.utils

import java.util.*


/**
 * @Description:
 *
 */

object OrderIdGenerateUtil {
    fun getRandomString(length: Int = 32): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val sb = StringBuffer()
        for (i in 0 until length) {
            val number: Int = random.nextInt(62)
            sb.append(str[number])
        }
        return sb.toString()
    }
}