# flutter_heart_beating

A new flutter plugin project.

## Getting Started

```yaml
flutter_heart_beating:
    git: git://github.com/scofield-hello/flutter_heart_beating_plugin.git
```

```dart
await FlutterHeartBeating().start(HeartBeatingConfig(
        "https://abcdefg.com/heartbeat",
        interval: 30,
        headers: {
          "Authorization":"anything"
        },
        body: {
          "a": "a",
          "b": "b"
        }));
```