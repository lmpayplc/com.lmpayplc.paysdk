package com.awesome.lemapaysdkdemo.common.utils

import android.content.Context
import android.os.Environment
import java.io.File


object FileUtils {
    private const val HTTP_CACHE_DIR = "http"
    fun getHttpCacheDir(context: Context): File {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            File(context.externalCacheDir, HTTP_CACHE_DIR)
        } else File(context.cacheDir, HTTP_CACHE_DIR)
    }
}
