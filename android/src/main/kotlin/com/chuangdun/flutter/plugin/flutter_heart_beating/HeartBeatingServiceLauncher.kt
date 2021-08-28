package com.chuangdun.flutter.plugin.flutter_heart_beating

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "HBServiceLauncher"

class HBServiceLauncher(private val context: Context, private val workerParams: WorkerParameters) : Worker(context, workerParams) {


    override fun doWork(): Result {
        Log.i(TAG, "doWork")
        val params = workerParams.inputData.keyValueMap
        val postUrl = params[ARG_POST_URL] as String
        val headers = params[ARG_POST_HEADERS] as String
        val body = params[ARG_POST_BODY] as String
        val interval = params[ARG_INTERVAL_SECONDS] as Int
        val notificationTitle = params[ARG_NOTIFICATION_TITLE] as String
        val notificationContent = params[ARG_NOTIFICATION_CONTENT] as String
        val intent = Intent(context, HeartBeatingService::class.java)
        intent.putExtra(ARG_COMMAND, SERVICE_ON)
        intent.putExtra(ARG_POST_URL, postUrl)
        intent.putExtra(ARG_POST_HEADERS, headers)
        intent.putExtra(ARG_POST_BODY, body)
        intent.putExtra(ARG_INTERVAL_SECONDS, interval)
        intent.putExtra(ARG_NOTIFICATION_TITLE, notificationTitle)
        intent.putExtra(ARG_NOTIFICATION_CONTENT, notificationContent)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        val intent = Intent(context, HeartBeatingService::class.java)
        intent.putExtra(ARG_COMMAND, SERVICE_OFF)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
        Log.d(TAG, "stopped")
    }

}