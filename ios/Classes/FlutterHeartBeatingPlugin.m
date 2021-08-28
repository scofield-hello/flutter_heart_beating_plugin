#import "FlutterHeartBeatingPlugin.h"
#import "HeartBeatingManager.h"

@implementation FlutterHeartBeatingPlugin{
    HeartBeatingManager *_heartBeatingManager;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"com.chuangdun.flutter.plugin/flutter_heart_beating"
            binaryMessenger:[registrar messenger]];
  FlutterHeartBeatingPlugin* instance = [[FlutterHeartBeatingPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"start" isEqualToString:call.method]) {
    NSDictionary *arguments = call.arguments;
    NSString *postUrl = [arguments objectForKey:@"postUrl"];
    int interval = ((NSNumber*)[arguments objectForKey:@"interval"]).intValue;
    NSMutableDictionary *headers = [[NSMutableDictionary alloc]initWithDictionary:((NSDictionary*)[arguments objectForKey:@"headers"])];
    NSMutableDictionary *body = [[NSMutableDictionary alloc]initWithDictionary:((NSDictionary*)[arguments objectForKey:@"body"])];
    if (!_heartBeatingManager) {
        _heartBeatingManager = [[HeartBeatingManager alloc]init];
    }
    [_heartBeatingManager start:postUrl
                    headers:headers
                       body:body
                   withInterval:interval];
  } else if ([@"stop" isEqualToString:call.method]) {
      if (_heartBeatingManager) {
          [_heartBeatingManager stop];
      }
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
