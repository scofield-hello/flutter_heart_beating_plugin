# flutter_heart_beating

A new flutter plugin project.

## Getting Started

```yaml
tracker:
    git: git://github.com/scofield-hello/flutter_tracker_plugin.git
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