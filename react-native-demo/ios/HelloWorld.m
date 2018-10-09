//
//  HelloWorld.m
//  kf5demo
//
//  Created by admin on 2018/9/26.
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import "HelloWorld.h"
#import <React/RCTLog.h>

@implementation HelloWorld

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(hello:(NSString *)name)
{
  RCTLogInfo(@"Hello %@ from IOS", name);
}

@end
