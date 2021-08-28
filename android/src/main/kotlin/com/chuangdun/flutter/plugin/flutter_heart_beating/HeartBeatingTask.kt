package com.chuangdun.flutter.plugin.flutter_heart_beating

import android.util.Log
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val TAG = "HeartBeatingTask"

class HeartBeatingTask(
    private val postUrl: String,
    private val headers: JSONObject,
    private val body: JSONObject) : Runnable {

    override fun run() {
        try {
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
            body.put("timestamp", System.currentTimeMillis())
            body.put("platform", "android")
            val requestBody = body.toString(4)
                .toRequestBody("application/json; charset=utf-8".toMediaType())
            val okHttpHeaderBuilder = Headers.Builder()
            headers.keys().forEach { okHttpHeaderBuilder.add(it, headers.getString(it)) }
            val request = Request.Builder()
                .url(postUrl)
                .headers(okHttpHeaderBuilder.build())
                .post(requestBody)
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.code == 200) {
                val json = JSONObject(response.body!!.string())
                Log.d(TAG, "响应：${json.toString(4)}")
                val code = json.getInt("code")
                if (200 == code) {
                    Log.d(TAG, "心跳包发送成功.")
                } else {
                    throw Exception("心跳包发送失败, 响应码: $code")
                }
            } else {
                throw Exception("心跳包请求发送失败, http status: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "心跳包发送失败", e)
        }
    }

}