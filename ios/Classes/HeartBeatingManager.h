//
//  HeartBeatingManager.h
//  flutter_heart_beating
//
//  Created by Nick on 2021/8/28.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface HeartBeatingManager : NSObject
-(void) start:(NSString*)postUrl headers:(NSMutableDictionary*)headers
     body:(NSMutableDictionary*)body withInterval:(int)interval;
-(void) stop;
@end

NS_ASSUME_NONNULL_END
