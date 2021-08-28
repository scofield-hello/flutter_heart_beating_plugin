import 'dart:async';

import 'package:flutter/services.dart';

class HeartBeatingConfig {
  ///指定心跳包上报接口地址.
  final String postUrl;

  ///指定心跳包上报携带的请求头.
  final Map<String, String> headers;

  ///指定心跳包上报携带的参数.
  final Map<String, String> body;

  ///发送心跳包的时间间隔(秒).
  final int interval;

  ///安卓常驻通知标题.
  final String notificationTitle;

  ///安卓常驻通知内容.
  final String notificationContent;

  HeartBeatingConfig(this.postUrl,
      {this.headers = const <String, String>{},
      this.body = const <String, String>{},
      this.interval = 300,
      this.notificationTitle = "核心服务已开启",
      this.notificationContent = "核心服务正在运行中..."})
      : assert(postUrl != null),
        assert(interval >= 5),
        assert(notificationTitle != null && notificationTitle.isNotEmpty),
        assert(notificationContent != null && notificationContent.isNotEmpty);

  Map<String, dynamic> asJson() {
    return <String, dynamic>{
      "postUrl": postUrl,
      "interval": interval,
      "headers": headers ?? <String, String>{},
      "body": body ?? <String, String>{},
      "notificationTitle": notificationTitle,
      "notificationContent": notificationContent
    };
  }
}

class FlutterHeartBeating {
  static FlutterHeartBeating _singleton;
  final MethodChannel _methodChannel;

  factory FlutterHeartBeating() {
    if (_singleton == null) {
      const MethodChannel methodChannel =
          MethodChannel('com.chuangdun.flutter.plugin/flutter_heart_beating');
      _singleton = FlutterHeartBeating.private(methodChannel);
    }
    return _singleton;
  }

  FlutterHeartBeating.private(this._methodChannel);

  Future<void> start(HeartBeatingConfig config) async {
    assert(config != null);
    await _methodChannel.invokeMethod("start", config.asJson());
  }

  Future<void> stop() async {
    await _methodChannel.invokeMethod("stop");
  }
}
