package com.john.shopifyApplication.common

import android.support.annotation.Nullable
import com.john.shopifyApplication.config.Constants
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

class HttpManager {

    companion object {
        const val TAG = Constants.BASICTAG + "HttpManager"
        var share = HttpManager()
    }

    private val timeout: Long = 60
    private val JSON = MediaType.parse("application/json; charset=utf-8")

    private var httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(timeout, TimeUnit.SECONDS)
        .build()


    fun delete(url: String, @Nullable token: String?, callback: JSONCallback) {
        val requestBuilder = Request.Builder()
            .url(url)
            .delete()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", token)
        }
        val request = requestBuilder.build()

        val call = httpClient.newCall(request)
        call.enqueue(ResponseCallback(callback))
    }

    fun get(url: String, @Nullable token: String?, callback: JSONCallback) {
        var requestBuilder = Request.Builder()
            .url(url)
            .get()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", token)
        }
        val request = requestBuilder.build()

        var call = httpClient.newCall(request)
        call.enqueue(ResponseCallback(callback))
    }

    fun post(url: String, @Nullable token: String?, parameters: String, callback: JSONCallback) {
        val body: RequestBody = RequestBody.create(JSON, parameters)

        val requestBuilder = Request.Builder()
            .url(url)
            .post(body)

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", token)
        }
        requestBuilder.addHeader("Content-Type", "application/json")
        val request = requestBuilder.build()

        val call = httpClient.newCall(request)
        call.enqueue(ResponseCallback(callback))
    }
}