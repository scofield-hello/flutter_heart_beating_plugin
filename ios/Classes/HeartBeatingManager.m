//
//  HeartBeatingManager.m
//  flutter_heart_beating
//
//  Created by Nick on 2021/8/28.
//

#import "HeartBeatingManager.h"
#import "AFNetworking.h"
#import <sys/utsname.h>

@interface HeartBeatingManager(){
    dispatch_source_t _timer;
}
@end

@implementation HeartBeatingManager

-(instancetype) init{
    self = [super init];
    return self;
}

-(void) start:(NSString*)postUrl headers:(NSMutableDictionary*)headers
     body:(NSMutableDictionary*)body withInterval:(int)interval{
    NSLog(@"heartbeat postUrl=%@", postUrl);
    NSLog(@"heartbeat headers=%@", headers);
    NSLog(@"heartbeat body=%@", body);
    NSLog(@"heartbeat interval=%d", interval);
    __weak HeartBeatingManager* weakManager = self;
    if (_timer) {
        dispatch_source_cancel(_timer);
        _timer = nil;
    }
    _timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, dispatch_get_global_queue(QOS_CLASS_BACKGROUND, 0));
    // 2. 设置定时器启动时间、间隔
    dispatch_source_set_timer(_timer, DISPATCH_TIME_NOW,
                              interval * NSEC_PER_SEC,  0 * NSEC_PER_SEC);
    // 3. 设置callback
    dispatch_source_set_event_handler(_timer, ^{
            NSLog(@"心跳服务已启动");
        [weakManager createDataTask:postUrl headers:headers body:body];
        });
    dispatch_source_set_cancel_handler(_timer, ^{
        //取消定时器时一些操作
        NSLog(@"心跳服务已停止");
    });
    // 4. 启动定时器（刚创建的source处于被挂起状态)
    dispatch_resume(_timer);
}

-(void) stop{
    if (_timer) {
        // 6. 取消定时器
        dispatch_source_cancel(_timer);
        _timer = nil;
    }
}

-(void)createDataTask:(NSString*)postUrl headers:(NSMutableDictionary*)headers
                 body:(NSMutableDictionary*)body {
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    AFSecurityPolicy *securityPolicy = [AFSecurityPolicy defaultPolicy];
    securityPolicy.allowInvalidCertificates = YES;
    securityPolicy.validatesDomainName = NO;
    manager.securityPolicy = securityPolicy;
    manager.responseSerializer = [AFJSONResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.requestSerializer.timeoutInterval = 30;
    [manager.requestSerializer setValue:@"application/json; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    long timestamp = [[NSNumber numberWithDouble:[NSDate date].timeIntervalSince1970] longValue] * 1000 ;
    [body setValue:[NSNumber numberWithLong:timestamp] forKey:@"timestamp"];
    [manager POST:postUrl parameters:body headers:headers progress:^(NSProgress * _Nonnull uploadProgress) {
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSDictionary *data = responseObject;
        if ([[data allKeys] containsObject:@"code"]) {
            int code = ((NSNumber*)[data objectForKey:@"code"]).intValue;
            if (code == 200 || code == 0) {
                NSLog(@"心跳包响应成功:%@", data);
            }else{
                NSLog(@"心跳包响应失败:%@", data);
            }
        }else{
            NSLog(@"心跳包已发送,响应:%@", data);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            NSLog(@"心跳包发送失败.");
    }];
}
@end
