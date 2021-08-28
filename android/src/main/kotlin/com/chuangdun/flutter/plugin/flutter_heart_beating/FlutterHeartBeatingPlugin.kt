package com.chuangdun.flutter.plugin.flutter_heart_beating

import android.app.Activity
import android.content.Context

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

private const val TAG = "HeartBeatingPlugin"
private const val METHOD_CHANNEL_NAME = "com.chuangdun.flutter.plugin/flutter_heart_beating"

class FlutterHeartBeatingPlugin: FlutterPlugin, MethodCallHandler, ActivityAware{

  private lateinit var channel : MethodChannel
  private var context: Context? = null
  private var activity: Activity? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, METHOD_CHANNEL_NAME)
    channel.setMethodCallHandler(this)
  }

  companion object {
    @JvmStatic
    fun registerWith(registrar: PluginRegistry.Registrar) {
      val heartBeatingPlugin = FlutterHeartBeatingPlugin()
      heartBeatingPlugin.context = registrar.context()
      heartBeatingPlugin.activity = registrar.activity()
      val channel = MethodChannel(registrar.messenger(), METHOD_CHANNEL_NAME)
      channel.setMethodCallHandler(heartBeatingPlugin)
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (activity == null || context == null) {
      result.success(false)
      return
    }
    when (call.method) {
      "start" -> {
        val postUrl = call.argument<String>(ARG_POST_URL)
        val headers = call.argument<Map<String, String>>(ARG_POST_HEADERS)
        val body = call.argument<Map<String, String>>(ARG_POST_BODY)
        val interval = call.argument<Int>(ARG_INTERVAL_SECONDS)
        val notificationTitle = call.argument<String>(ARG_NOTIFICATION_TITLE)
        val notificationContent = call.argument<String>(ARG_NOTIFICATION_CONTENT)
        if (postUrl == null || headers == null || body == null || interval == null
          || notificationTitle == null || notificationContent == null) {
          throw IllegalArgumentException("心跳服务参数有误,请检查.")
        }
        HeartBeatingManager.start(context!!,
          postUrl,
          headers,
          body,
          interval,
          notificationTitle,
          notificationContent)
        result.success(true)
      }
      "stop" -> {
        HeartBeatingManager.shutdown(context!!)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    this.activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    this.activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    this.activity = null
  }
}
