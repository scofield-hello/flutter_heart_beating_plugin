package com.chuangdun.flutter.plugin.flutter_heart_beating

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.*

import org.json.JSONObject
import java.util.concurrent.TimeUnit

class HeartBeatingManager {
    companion object {
        private const val TAG = "HeartBeatingManager"
        private const val UNIQUE_WORK_NAME = "HBServiceLauncher"
        @JvmStatic
        fun start(context: Context, postUrl: String, headers: Map<String, String>,
                  body: Map<String, String>, interval: Int,
                  notificationTitle: String, notificationContent: String) {
            val params = Data.Builder()
                    .putString(ARG_POST_URL, postUrl)
                    .putString(ARG_POST_HEADERS, JSONObject(headers).toString(4))
                    .putString(ARG_POST_BODY, JSONObject(body).toString(4))
                    .putString(ARG_NOTIFICATION_TITLE, notificationTitle)
                    .putString(ARG_NOTIFICATION_CONTENT, notificationContent)
                    .putInt(ARG_INTERVAL_SECONDS, interval)
                    .build()
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val workRequest = PeriodicWorkRequest.Builder(
                    HBServiceLauncher::class.java, 15, TimeUnit.MINUTES)
                    .setInputData(params)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(UNIQUE_WORK_NAME,
                            ExistingPeriodicWorkPolicy.REPLACE, workRequest)
            Log.i(TAG, "心跳服务定时任务已开启.")
        }

        @JvmStatic
        fun shutdown(context: Context) {
            val intent = Intent(context, HeartBeatingService::class.java)
            intent.putExtra(ARG_COMMAND, SERVICE_OFF)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(intent)
            } else {
                context.startForegroundService(intent)
            }
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
            Log.i(TAG, "心跳服务定时任务已取消.")
        }
    }
}