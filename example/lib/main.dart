import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_heart_beating/flutter_heart_beating.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  Future<void> start() async {
    if (!mounted) return;
    await FlutterHeartBeating().start(HeartBeatingConfig(
        "http://bk.lookmap.net/cc/mobile/onlineUsers/add",
        interval: 30,
        headers: {"Cookie": "JSESSIONID=D778974CC32E6021F924BE6E6A0986DC"},
        body: {"peopleType": "0", "peoplePk": "1"}));
  }

  Future<void> restart() async {
    if (!mounted) return;
    await FlutterHeartBeating().start(HeartBeatingConfig(
        "http://192.168.0.18:8080/mob/system/onlineUsers/create",
        interval: 60,
        headers: {
          "Authorization":
              "Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjVhYjJiZWVlLTA1N2MtNGM0OS1hYmUyLWI1Y2QwM2NlYmRlNiJ9.IusCiRjAdggWEEjtFdHNa7XdYWqoHB-d9y9wEpRalQ-Cy9QSY9VQOl9G2xF2T2ogPXLuspfY8iti5k0vIxDDYw"
        },
        body: {
          "peopleType": "1",
          "peoplePk": "11"
        }));
  }

  Future<void> stop() async {
    if (!mounted) return;
    await FlutterHeartBeating().stop();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              RaisedButton(
                onPressed: () {
                  start();
                },
                child: Text("打开"),
              ),
              RaisedButton(
                onPressed: () {
                  restart();
                },
                child: Text("更新"),
              ),
              RaisedButton(
                onPressed: () {
                  stop();
                },
                child: Text("关闭"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
