package com.awesome.lemapaysdkdemo.api

import java.io.Serializable


/**
 * @Description:
 *
 */

data class BaseResponse<T>(
    val code: Int = 0,
    val msg: String = "",
    val data: T
) : Serializable {
    fun isSuccess(): Boolean {
        return this.code == 0
    }
}

data class OrderDetail(val transaction_id: String) : Serializable