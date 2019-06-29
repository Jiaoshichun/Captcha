package com.heng.captcha.puzzle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

/**
 * 加载网络图片
 */
class BitmapLoaderManager(private val callBack: ((bitmap: Bitmap?) -> Unit)) : AsyncTask<String, Void, Bitmap?>() {

    override fun doInBackground(vararg params: String?): Bitmap? {
        val url = params.firstOrNull()
        if (url.isNullOrBlank()) return null
        var connection: HttpURLConnection? = null
        try {
            connection = URL(url).openConnection() as HttpURLConnection?
            connection?.requestMethod = "GET"
            connection?.connect()
            if (connection?.responseCode == 200) connection.inputStream.use {
                return BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        callBack.invoke(result)
    }
}