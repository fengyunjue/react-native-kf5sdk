//
//  RCTKF5SDK.m
//  RCTKF5SDK
//
//  Created by admin on 2018/9/26.
//  Copyright © 2018年 kf5. All rights reserved.
//

#import "RCTKF5SDK.h"
#import "KF5SDK/KF5SDK.h"
#import <objc/runtime.h>

@interface NSDictionary (KF5SDK)

- (NSInteger)integerValueForKey:(NSString *)key defaultValue:(NSInteger)defaultValue;
- (int)intValueForKey:(NSString *)key defaultValue:(int)defaultValue;
- (long long)longlongValueForKey:(NSString *)key defaultValue:(long long)defaultValue;
- (float)floatValueForKey:(NSString *)key defaultValue:(float)defaultValue;
- (BOOL)boolValueForKey:(NSString *)key defaultValue:(BOOL)defaultValue;
- (NSString *)stringValueForKey:(NSString *)key defaultValue:(NSString *)defaultValue;
- (NSArray *)arrayValueForKey:(NSString *)key defaultValue:(NSArray *)defaultValue;
- (NSDictionary *)dictValueForKey:(NSString *)key defaultValue:(NSDictionary *)defaultValue;

@end

static NSDictionary *localizedDict = nil;

// 是否显示右侧按钮
static BOOL RightTextVisible = YES;

@interface RCTKF5SDK()

@property (nonatomic, strong) UIColor *navColor;
@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, strong) UIFont *centerTextFont;

@property (nonatomic, weak) KFChatViewController *chat;
@end

@implementation RCTKF5SDK

RCT_EXPORT_MODULE();

#pragma mark - 初始化
RCT_EXPORT_METHOD(initKF5:(NSDictionary *)paramDict completion:(RCTResponseSenderBlock)callback){
    // 设置语言
    [KFHelper setLocalLanguage:[paramDict stringValueForKey:@"language" defaultValue:nil]];
    // 云客服地址
    NSString *hostName = [paramDict stringValueForKey:@"hostName" defaultValue:nil];
    // 云客服密钥
    NSString *appId = [paramDict stringValueForKey:@"appId" defaultValue:nil];
    // 初始化公司
    [KFUserManager initializeWithHostName:hostName appId:appId];
    
    // 邮箱
    NSString *email = [paramDict stringValueForKey:@"email" defaultValue:@""];
    // 昵称
    NSString *userName = [paramDict stringValueForKey:@"userName" defaultValue:nil];
    // 手机号
    NSString *phone = [paramDict stringValueForKey:@"phone" defaultValue:@""];
    
    // 推送token
    NSString *deviceToken = [paramDict stringValueForKey:@"deviceToken" defaultValue:nil];
    // 用户验证方式
    NSInteger userType = [paramDict integerValueForKey:@"verifyUserType" defaultValue:1];
    
    BOOL isPhone = NO;
    if (phone.length != 0 && email.length != 0) {
        isPhone = userType == 2;
    }else{
        isPhone = phone.length > 0;
    }
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:2];
    [params setObject:@"react-native" forKey:@"source"];
    if (isPhone) {
        [params setObject:phone forKey:KF5Phone];
    }else{
        [params setObject:email forKey:KF5Email];
    }
    __weak typeof(self)weakSelf = self;
    [[KFUserManager shareUserManager]initializeWithParams:params completion:^(KFUser * _Nullable user, NSError * _Nullable error) {
        if (error) {
            if (callback) {
                callback(@[@{@"error":@(error.code), @"message":error.domain}]);
            }
        }else{
            if (callback) {
                callback(@[@{@"error":@(0), @"message":@"初始化成功"}]);
            }
            [weakSelf updateUser:user isPhone:isPhone email:email phone:phone name:userName deviceToken:deviceToken];
        }
    }];
}


#pragma mark - 显示帮助中心
RCT_EXPORT_METHOD(showHelpCenter:(NSDictionary *)paramDict){
    int type = (int)[paramDict integerValueForKey:@"type" defaultValue:3];
    UIViewController *view = nil;
    if (type == 0 || type == 1) {
        view = [[KFCategorieListViewController alloc] init];
    }else if (type == 2) {
        view = [[KFForumListViewController alloc] init];
    }else{
        view = [[KFPostListViewController alloc] init];
    }
    [self presentViewController:view];
}



#pragma mark - 显示创建工单控制器
RCT_EXPORT_METHOD(showRequestCreation){
    KFCreateTicketViewController *creatView = [[KFCreateTicketViewController alloc]init];
    [self presentViewController:creatView];
}

#pragma mark - 显示工单列表
RCT_EXPORT_METHOD(showRequestList){
    KFTicketListViewController *ticketList = [[KFTicketListViewController alloc] init];
    ticketList.isHideRightButton = !RightTextVisible;
    [self presentViewController:ticketList];
}

#pragma mark - 显示聊天界面
RCT_EXPORT_METHOD(showChatView:(NSDictionary *)paramDict){
    // 用户自定义字段
    NSArray *metadata = [paramDict arrayValueForKey:@"metadata" defaultValue:nil];
    KFChatViewController *chat = [[KFChatViewController alloc]initWithMetadata:metadata];
    
    BOOL isShowAlertWhenNoAgent = [paramDict boolValueForKey:@"isShowAlertWhenNoAgent" defaultValue:YES];
    
    NSString *ratingAlertTitle = [paramDict stringValueForKey:@"ratingAlertTitle" defaultValue:nil];
    NSString *ratingFinishSystemTitle = [paramDict stringValueForKey:@"ratingFinishSystemTitle" defaultValue:nil];
    NSString *connectingShowTitle = [paramDict stringValueForKey:@"connectingShowTitle" defaultValue:nil];
    NSString *connectErrorShowTitle = [paramDict stringValueForKey:@"connectErrorShowTitle" defaultValue:nil];
    NSString *getAgentingShowTitle = [paramDict stringValueForKey:@"getAgentingShowTitle" defaultValue:nil];
    NSString *noAgentShowTitle = [paramDict stringValueForKey:@"noAgentShowTitle" defaultValue:nil];
    NSString *chatEndShowTitle = [paramDict stringValueForKey:@"chatEndShowTitle" defaultValue:nil];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithCapacity:8];
    
    if (ratingAlertTitle)
        [dict setValue:ratingAlertTitle forKey:@"kf5_rating_content"];
    if (ratingFinishSystemTitle)
        [dict setValue:ratingFinishSystemTitle forKey:@"kf5_rating_successfully"];
    if (connectingShowTitle)
        [dict setValue:connectingShowTitle forKey:@"kf5_connecting"];
    if (connectErrorShowTitle)
        [dict setValue:connectErrorShowTitle forKey:@"kf5_not_connected"];
    if (getAgentingShowTitle)
        [dict setValue:getAgentingShowTitle forKey:@"kf5_queue_waiting"];
    if (noAgentShowTitle)
        [dict setValue:noAgentShowTitle forKey:@"kf5_no_agent_online"];
    if (chatEndShowTitle)
        [dict setValue:chatEndShowTitle forKey:@"kf5_chat_ended"];
    
    localizedDict = dict;
    
    // 设置内容
    chat.showAlertWhenNoAgent = isShowAlertWhenNoAgent;
    // 是否隐藏右侧按钮
    chat.isHideRightButton = !RightTextVisible;
    
    [self presentViewController:chat];
    self.chat = chat;
}
#pragma mark - 设置没有客服时处理回调
//当没有客服在线或取消排队留言时,弹出alertView,点击"确定"按钮的事件处理,默认跳转到反馈工单界面
RCT_EXPORT_METHOD(noAgentAlertActionBlock:(RCTResponseSenderBlock)callback){
    self.chat.noAgentAlertActionBlock = ^{
        dispatch_async(dispatch_get_main_queue(), ^{
            if (callback) {
                callback(@[]);
            }
        });
    };
}

#pragma mark - 设置工单自定义字段
RCT_EXPORT_METHOD(setCustomFields:(NSDictionary *)paramDict){
    [KFCreateTicketViewController setCustomFields:[paramDict arrayValueForKey:@"custom_fields" defaultValue:nil]];
}

#pragma mark - 设置navBar的颜色
RCT_EXPORT_METHOD(setTopBarColor:(NSDictionary *)paramDict){
    self.navColor = [self colorFromNSString:[paramDict stringValueForKey:@"navColor" defaultValue:@"#3E4245"]];
    self.textColor = [self colorFromNSString:[paramDict stringValueForKey:@"textColor" defaultValue:@"#FFFFFF"]];
    self.centerTextFont = [UIFont boldSystemFontOfSize:([paramDict intValueForKey:@"centerTextSize" defaultValue:22]- 4)];
    // 其他的控制分布在控制器的设置上
    RightTextVisible = [paramDict boolValueForKey:@"rightTextVisible" defaultValue:YES];
    // 是否显示文档控制器右侧按钮
    [KFDocBaseViewController setIsHideRightButton:!RightTextVisible];
}
    

#pragma mark - 私有方法
    
#pragma mark 更新用户信息
- (void)updateUser:(KFUser *)user isPhone:(BOOL)isPhone  email:(NSString *)email phone:(NSString *)phone name:(NSString *)name deviceToken:(NSString *)deviceToken{
    
    NSString *new_email = isPhone ? ([user.email isEqualToString:email]?nil:email) : nil;
    NSString *new_phone = isPhone ? nil : ([user.phone isEqualToString:phone]?nil:phone);
    NSString *new_name = [user.userName isEqualToString:name]?nil:name;
    if (new_email.length > 0 || new_phone.length > 0 || new_name.length > 0) {
        [[KFUserManager shareUserManager]updateUserWithEmail:new_email phone:new_phone name:new_name completion:nil];
    }
    
    NSArray *deviceTokens = [user.deviceTokens arrayValueForKey:@"IOS" defaultValue:nil];
    if (deviceToken.length > 0 && ![deviceTokens containsObject:deviceToken]) {
        [[KFUserManager shareUserManager]saveDeviceToken:deviceToken completion:nil];
    }
}

- (void)presentViewController:(UIViewController *)viewController {
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:viewController];
    viewController.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"关闭" style:UIBarButtonItemStylePlain target:self action:@selector(back:)];
    viewController.hidesBottomBarWhenPushed = YES;
    [nav.navigationBar setTintColor:self.textColor];
    [nav.navigationBar setBarTintColor:self.navColor];
    [nav.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName:self.textColor,NSFontAttributeName:self.centerTextFont}];
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIApplication sharedApplication].keyWindow.rootViewController presentViewController:nav animated:YES completion:nil];
    });
}

- (void)back:(id)sender {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIApplication sharedApplication].keyWindow.rootViewController dismissViewControllerAnimated:YES completion:nil];
    });
}

- (UIColor *)colorFromNSString:(NSString*)colorString{
    NSString *color = nil;
    NSNumber *argb = nil;
    if ([colorString characterAtIndex:0] == '#') {
        if (colorString.length == 7) {
            color = @"0xFF";
            color = [color stringByAppendingString:[colorString substringFromIndex:1]];
            argb = [[NSNumber alloc] initWithUnsignedLong:strtol([color UTF8String], 0, 16)];
            
            return [RCTConvert UIColor:argb];
        }else if (colorString.length == 9) {
            color = @"0x";
            color = [color stringByAppendingString:[colorString substringFromIndex:1]];
            argb = [[NSNumber alloc] initWithUnsignedLong:strtol([color UTF8String], 0, 16)];
            
            return [RCTConvert UIColor:argb];
        }
    }
    return nil;
}

@end


@implementation NSDictionary (KF5SDK)

- (NSInteger)integerValueForKey:(NSString *)key defaultValue:(NSInteger)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        return ((NSNumber *)[self valueForKey:key]).integerValue;
    }
    return defaultValue;
}

- (int)intValueForKey:(NSString *)key defaultValue:(int)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        return ((NSNumber *)[self valueForKey:key]).intValue;
    }
    return defaultValue;
}

- (long long)longlongValueForKey:(NSString *)key defaultValue:(long long)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        return ((NSNumber *)[self valueForKey:key]).longLongValue;
    }
    return defaultValue;
}

- (float)floatValueForKey:(NSString *)key defaultValue:(float)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        return ((NSNumber *)[self valueForKey:key]).floatValue;
    }
    return defaultValue;
}

- (BOOL)boolValueForKey:(NSString *)key defaultValue:(BOOL)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        return ((NSNumber *)[self valueForKey:key]).boolValue;
    }
    return defaultValue;
}
- (NSString *)stringValueForKey:(NSString *)key defaultValue:(NSString *)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        return [NSString stringWithFormat:@"%@", [self valueForKey:key]];
    }
    return defaultValue;
}
- (NSArray *)arrayValueForKey:(NSString *)key defaultValue:(NSArray *)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        NSArray *obj =  [self valueForKey:key];
        if ([obj isKindOfClass:[NSArray class]]) {
            return obj;
        }
    }
    return defaultValue;
}
- (NSDictionary *)dictValueForKey:(NSString *)key defaultValue:(NSDictionary *)defaultValue{
    if ([[self allKeys] containsObject:key]) {
        NSDictionary *obj =  [self valueForKey:key];
        if ([obj isKindOfClass:[NSDictionary class]]) {
            return obj;
        }
    }
    return defaultValue;
}

@end




@interface NSObject(KF5SDK)
+ (BOOL)apicloud_swizzleMethod:(SEL)origSel withMethod:(SEL)altSel;
+ (BOOL)apicloud_swizzleClassMethod:(SEL)origSel withClassMethod:(SEL)altSel;
@end

@interface KFHelper(KF5SDK)
@end

@implementation KFHelper(KF5SDK)

+ (void)load
{
    [self apicloud_swizzleClassMethod:@selector(localizedStringForKey:value:) withClassMethod:@selector(apicloud_localizedStringForKey:value:)];
}

+ (NSString *)apicloud_localizedStringForKey:(NSString *)key value:(NSString *)value{
    
    if (key == nil) return @"";
    
    if ([[localizedDict allKeys]containsObject:key]) {
        return [localizedDict valueForKey:key];
    }else{
        return [self apicloud_localizedStringForKey:key value:value];
    }
}

@end

@interface KFBaseTableViewController(KF5SDK)
@end
@implementation KFBaseTableViewController(KF5SDK)
+ (void)load
{
    [self apicloud_swizzleMethod:@selector(viewWillAppear:) withMethod:@selector(apicloud_viewWillAppear:)];
    [self apicloud_swizzleMethod:@selector(viewWillDisappear:) withMethod:@selector(apicloud_viewWillDisappear:)];
}

- (void)apicloud_viewWillAppear:(BOOL)animated
{
    [self apicloud_viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:NO];
}

- (void)apicloud_viewWillDisappear:(BOOL)animated {
    [self apicloud_viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

@end

@interface KFBaseViewController(KF5SDK)
@end
@implementation KFBaseViewController(KF5SDK)
+ (void)load
{
    [self apicloud_swizzleMethod:@selector(viewWillAppear:) withMethod:@selector(apicloud_viewWillAppear:)];
    [self apicloud_swizzleMethod:@selector(viewWillDisappear:) withMethod:@selector(apicloud_viewWillDisappear:)];
}

- (void)apicloud_viewWillAppear:(BOOL)animated
{
    [self apicloud_viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:NO];
}

- (void)apicloud_viewWillDisappear:(BOOL)animated {
    [self apicloud_viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

@end


@implementation NSObject(KF5SDK)

+ (BOOL)apicloud_swizzleMethod:(SEL)origSel withMethod:(SEL)altSel
{
    Method originMethod = class_getInstanceMethod(self, origSel);
    Method newMethod = class_getInstanceMethod(self, altSel);
    
    if (originMethod && newMethod) {
        if (class_addMethod(self, origSel, method_getImplementation(newMethod), method_getTypeEncoding(newMethod))) {
            class_replaceMethod(self, altSel, method_getImplementation(originMethod), method_getTypeEncoding(originMethod));
        } else {
            method_exchangeImplementations(originMethod, newMethod);
        }
        return YES;
    }
    return NO;
}

+ (BOOL)apicloud_swizzleClassMethod:(SEL)origSel withClassMethod:(SEL)altSel
{
    Class c = object_getClass((id)self);
    return [c apicloud_swizzleMethod:origSel withMethod:altSel];
}

@end
