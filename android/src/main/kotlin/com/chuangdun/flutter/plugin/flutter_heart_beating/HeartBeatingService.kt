package com.chuangdun.flutter.plugin.flutter_heart_beating

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import org.json.JSONObject
import java.util.concurrent.*

const val SERVICE_OFF = 0
const val SERVICE_ON = 1

const val ARG_COMMAND = "command"
const val ARG_POST_URL = "postUrl"
const val ARG_POST_HEADERS = "headers"
const val ARG_POST_BODY = "body"
const val ARG_INTERVAL_SECONDS = "interval"
const val ARG_NOTIFICATION_TITLE = "notificationTitle"
const val ARG_NOTIFICATION_CONTENT = "notificationContent"

private const val TAG = "HeartBeatingService"
private const val NOTIFICATION_ID = 10024
private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "heart_beating_service"
private const val DEFAULT_NOTIFICATION_CHANNEL_NAME = "核心服务"
private const val DEFAULT_NOTIFICATION_DESCRIPTION = DEFAULT_NOTIFICATION_CHANNEL_NAME
private const val DEFAULT_NOTIFICATION_TITLE = "核心服务正在运行中"
private const val DEFAULT_NOTIFICATION_CONTENT = "核心服务正在运行中..."
private const val DEFAULT_INTERVAL_SECONDS = 300

class HeartBeatingService  : Service(){
    

    private lateinit var postUrl: String
    private lateinit var headers: JSONObject
    private lateinit var body: JSONObject
    private lateinit var notificationTitle: String
    private lateinit var notificationContent: String
    private var interval = DEFAULT_INTERVAL_SECONDS
    private var threadPool: ScheduledExecutorService?=null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "心跳服务组件已创建.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent!!.getIntExtra(ARG_COMMAND, SERVICE_OFF)) {
            SERVICE_ON -> {
                Log.i(TAG, "正在开启核心服务组件...")
                postUrl = intent.getStringExtra(ARG_POST_URL)!!
                headers = JSONObject(intent.getStringExtra(ARG_POST_HEADERS)!!)
                body = JSONObject(intent.getStringExtra(ARG_POST_BODY)!!)
                interval = intent.getIntExtra(ARG_INTERVAL_SECONDS, DEFAULT_INTERVAL_SECONDS)
                notificationTitle = intent.getStringExtra(ARG_NOTIFICATION_TITLE) !!
                notificationContent = intent.getStringExtra(ARG_NOTIFICATION_CONTENT)!!
                Log.d(TAG, "postUrl:$postUrl")
                Log.d(TAG, "headers: $headers")
                Log.d(TAG, "body: $body")
                Log.d(TAG, "interval: $interval 秒")
                Log.d(TAG, "notificationTitle: $notificationTitle")
                Log.d(TAG, "notificationContent: $notificationContent")
                createNotification(this, notificationTitle, notificationContent)
                threadPool?.shutdownNow()
                threadPool = Executors.newScheduledThreadPool(1,
                    ThreadFactory { r -> Thread(r, "heart_beating_thread") })
                threadPool!!.scheduleAtFixedRate(HeartBeatingTask(postUrl, headers, body),
                    0, interval.toLong(), TimeUnit.SECONDS)
                Log.i(TAG, "心跳服务组件已开启.")
            }
            SERVICE_OFF -> {
                Log.i(TAG, "正在关闭心跳服务组件...")
                createNotification(this,
                    DEFAULT_NOTIFICATION_TITLE,
                    DEFAULT_NOTIFICATION_CONTENT)
                stopForeground(true)
                stopSelf()
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun createNotification(context: Service,
                                   notificationTitle: String,
                                   notificationContent: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            createNotificationPreO(context, notificationTitle, notificationContent)
        } else {
            createNotificationO(context, notificationTitle, notificationContent)
        }
    }

    @TargetApi(26)
    fun createNotificationO(context: Service,
                            notificationTitle: String,
                            notificationContent: String) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID,
            DEFAULT_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description = DEFAULT_NOTIFICATION_DESCRIPTION
        notificationManager.createNotificationChannel(notificationChannel)
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val piLaunchMainActivity = PendingIntent.getActivity(context,
            10002, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = Notification.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setContentIntent(piLaunchMainActivity)
            .setStyle(Notification.BigTextStyle())
            .setOngoing(true)
            .build()
        context.startForeground(
            NOTIFICATION_ID, notification)
    }

    @TargetApi(25)
    fun createNotificationPreO(context: Service,
                               notificationTitle: String,
                               notificationContent: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val piLaunchMainActivity = PendingIntent.getActivity(context,
            10002, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mNotification: Notification = NotificationCompat.Builder(context)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setContentIntent(piLaunchMainActivity)
            .setStyle(NotificationCompat.BigTextStyle())
            .build()
        context.startForeground(NOTIFICATION_ID, mNotification)
    }

    override fun onDestroy() {
        super.onDestroy()
        threadPool?.shutdown()
        Log.i(TAG, "心跳服务组件已销毁.")
    }
}