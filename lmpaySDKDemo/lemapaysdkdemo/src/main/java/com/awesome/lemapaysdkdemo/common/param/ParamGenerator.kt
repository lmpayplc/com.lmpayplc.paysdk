package com.laoshan.pospay.common.param


import com.awesome.lemapaysdkdemo.moudles.app.App.Companion.gson
import java.math.BigDecimal
import kotlin.collections.HashMap

object ParamGenerator {
    fun createBuilder(): Builder {
        return Builder()
    }

    class Builder {
        private val param by lazy { HashMap<String, Any>() }
        fun add(key: String, value: String): Builder {
            param[key] = value
            return this
        }

        fun add(key: String, value: BigDecimal): Builder {
            param[key] = value
            return this
        }

        fun add(key: String, value: Double): Builder {

            param[key] = value
            return this
        }

        fun add(key: String, value: Boolean): Builder {
            param[key] = value
            return this
        }

        fun add(key: String, value: Int): Builder {
            param[key] = value
            return this
        }

        fun add(key: String, value: Map<*, *>): Builder {
            param[key] = value
            return this
        }


        fun build(): String {
            return gson.toJson(param)
        }

        fun add(key: String, value: List<*>): Builder {
            param[key] = value
            return this
        }

        fun buildGet(): String {
            val sb = StringBuffer("?")
            val entries: Set<Map.Entry<String, Any>> = param.entries
            for ((key, value) in entries) {
                sb.append(key)
                    .append("=")
                sb.append(value)
                sb.append("&")
            }
            return sb.substring(
                0,
                sb.length - 1
            )
        }

        fun buildEncodeGet(): String {
            val sb = StringBuffer("?")
            val entries: Set<Map.Entry<String, Any>> = param.entries
            for ((key, value) in entries) {
                sb.append(key)
                    .append("=")
                sb.append(value)
                sb.append("&")
            }
            return sb.substring(0, sb.length - 1)
        }

        fun buildByteArray(): ByteArray {
            return gson.toJson(param).toByteArray()
        }
    }
}