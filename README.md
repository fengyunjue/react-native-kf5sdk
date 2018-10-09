# 云客服 React-Native 模块集成指南
## KF5SDK
##一、简介

kf5提供给开发者发送工单、查看工单列表、查看知识库等功能。本模块封装了kf5的相关接口，使用此模块需先注册kf5来获取appid和hostName。注册kf5：登录kf5官网( www.kf5.com )注册kf5账号,进入控制面板 - 系统设置 - 支持渠道 - 移动APP SDK中添加一个APP以获取appid，hostName为你注册的域名，例如：kf5.kf5.com。本模块支持iOS8.0以上和Android 4.1以上。

##二、安装与配置

###一、安装

```
npm install react-native-kf5sdk
```

即把react-native-kf5sdk导入到工程中的node_modules文件夹中

###二、配置

#### 1、iOS

通过rnpm link
如果你还没有安装[rnpm](https://github.com/rnpm/rnpm)，执行以下命令来安装rnpm

```
npm install -g rnpm
```
安装完rnpm后执行以下命令
```
rnpm link react-native-kf5sdk
```
link成功命令行会提示
```
rnpm info Linking react-native-kf5sdk ios dependency
```
或者你还可以手动配置
```
打开XCode's工程中, 右键点击Libraries文件夹 ➜ Add Files to <...> b.去node_modules ➜ react-native-kf5sdk ➜ ios ➜ 选择 RCTKF5SDK.xcodeproj c.在工程Build Phases ➜ Link Binary With Libraries中添加libRCTKF5SDK.a
```

工程配置
```
在工程target的Build Phases->Link Binary with Libraries中加入
libsqlite3.tbd
JavaScriptCore.framework
Photos.framework
AssetsLibrary.framework
等依赖库
```
如果在原生代码中需要引入KF5SDK
```
在工程target的Build Setting->Library Search Paths中添加$(SRCROOT)/../node_modules/react-native-kf5sdk/ios/RCTKF5SDK、在Header Search Paths中添加$(SRCROOT)/../node_modules/react-native-kf5sdk/ios/RCTKF5SDK
```
#####注意：SDK需要使用以下权限,进入工程中的info.plist，添加以下权限  

Privacy - Camera Usage Description：我们需要拍摄照片发送图片,是否允许打开相机？    
Privacy - Microphone Usage Description：我们需要录音发送语音消息,是否允许开启麦克风?  
Privacy - Photo Library Usage Description：我们需要为您展示图片列表,是否允许访问媒体资料库？   
![privacy.png](http://upload-images.jianshu.io/upload_images/1429831-f6849f289bb5edad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####2、Android

手动配置
编辑android/settings.gradle

```
// ...
include ':app'
include ':react-native-kf5sdk'
project(':react-native-kf5sdk').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-kf5sdk/android')
```
编辑android/app/build.gradle
```
// ...

dependencies {
    // ...
    compile project(':react-native-kf5sdk')
}
```
在MainApplication.java中注册模块（基于React-Native 0.32+）
编辑android/app/src/main/java/[...]/MainApplication.java
```
// ...

import com.kf5.rn.KF5SdkPackage;    // <--- 导包

public class MainApplication extends Application implements ReactApplication {
    // ...

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        // ...

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                new KF5SdkPackage()
            );
        }

        // ...
    };

    // ...
}
```
如果你使用的React-Native版本中不包含MainApplication.java，则可能需要在MainActivity.java中注册模块，方法同上。

##三、方法接口描述

react-native-kf5sdk提供一下方法：

- initKF5
- showHelpCenter
- showRequestCreation
- showRequestList
- showChatView
- setCustomFields
- setTopBarColor

#### * initKF5
初始化kf5 
```
initKF5({params}, callback(result)) 
```

##### params
| 参数名         | 类型   | 是否必填 | 描述                                                         |
| :------------- | :----- | :------- | :----------------------------------------------------------- |
| hostName       | 字符串 | 是       | 注册后的域名                                                 |
| appId          | 字符串 | 是       | 注册kf5后，从后台添加APP后获取                               |
| email          | 字符串 | 可选     | (手机和邮箱,必须传其一)用户的邮箱                            |
| phone          | 字符串 | 可选     | (手机和邮箱,必须传其一)用户的手机号                          |
| verifyUserType | 整型   | 可选     | 用户信息验证方式,只能传1(验证邮箱)或2(验证手机号),只有当邮箱和手机号同时传入时才生效,默认为1(验证邮箱)<br/>- 默认值：1/2 |
| deviceToken    | 字符串 | 可选     | 应用推送的deviceToken                                        |
| userName       | 字符串 | 可选     | 用户的昵称                                                   |

##### callback(result)

| 参数名 | 类型      | 内部字段                                                     |
| :----- | :-------- | :----------------------------------------------------------- |
| result | JSON 对象 | { <br/> "error" : 0, // 如果大于0，则说明初始化失败<br> "message" : "初始化成功" // 如果error大于0，则说明错误信息<br/> } |

##### 示例代码

```javascript
import kf5sdk from 'react-native-kf5sdk';

var param = {  
    hostName : "xxx.kf5.com",  
    appId : "xxxxxxx",  
    email : "",  
    phone : "",
    verifyUserType : 1,
    deviceToken : "",
    userName : "",
};  
kf5sdk.initKF5(param, callback);

callback(result) {
    Alert.alert(result.message);
}
```

##### 补充说明  
使用此模块，必须先用initKF5进行初始化，初始化之前无法使用此模块的其他方法。手机和邮箱必须格式正确。
> 1.email和phone必须传其一,如果单独传入email或者phone,则verifyUserType无效;     
> 2.如果email和phone都传入时以verifyPriorityType(用户信息验证方式)为主;     
>
> > 2.1 如果verifyUserType设置为1,则验证kf5系统中有没有该email,有则验证成功,并修改该用户的手机号(如果手机号已在系统中存在,将不会修改);    
> > 2.2 如果verifyUserType设置为2,则验证kf5系统中有没有该phone,有则验证成功;     
> > > 2.2.1 如果该用户不存在email则添加该email(如果该email已在系统中存在,将不会添加);     
> > > 2.2.1 如果该用户存在email,则email不能修改;    

#### * showHelpCenter
弹出kf5帮助文档页面
```javascript
showHelpCenter({params})  
```

##### params
| 参数名 | 类型 | 是否必填 | 描述                                                         |
| :----- | :--- | :------- | :----------------------------------------------------------- |
| type   | 整型 | 可选     | 显示帮助文档的方式，为0展示分区列表(默认)，为1直接展示分区列表，为2直接展示所有分类列表，为3直接展示所有文档列表 |

##### 示例代码
```javascript
import kf5sdk from 'react-native-kf5sdk';

var params = {
    type: 0
};
kf5sdk.showHelpCenter(params);  
```

#####补充说明  

使用此接口,必须先使用initKF5进行初始化。 

#### * showRequestCreation

弹出kf5反馈问题页面 
```javascript
showRequestCreation()  
```

##### 示例代码

```javascript
import kf5sdk from 'react-native-kf5sdk';

kf5sdk.showRequestCreation();  
```

##### 补充说明  

使用此接口，必须先使用initKF5进行初始化。 使用此接口需添加相机和照片的权限。 

#### * showRequestList
弹出kf5查看反馈页面 
```javascript
showRequestList() 
```

##### 示例代码
```javascript
import kf5sdk from 'react-native-kf5sdk';

kf5sdk.showRequestList();  
```

##### 补充说明  

使用此接口，必须先使用initKF5进行初始化。 

#### * showChatView

弹出kf5即时交谈
```
showChatView({params})
```

##### params
| 参数名   | 类型 | 是否必填 | 描述       |
| :------- | :--- | :------- | :--------- |
| metadata | 数组 | 可选     | IM自定义信 |

##### 示例代码
```javascript
import kf5sdk from 'react-native-kf5sdk';

var params = {
    metadata  : [
        {
            "name":"姓名",
            "value":"小明"
        },
        {
            "name":"性别",
            "value":"男"
        }
    ],
}; 
kf5sdk.showChatView(params);  
```

##### 补充说明  

使用此接口，必须先使用initKF5进行初始化。 使用此接口需添加相机、麦克风和照片的权限。 

#### * setCustomFields

设置工单自定义字段
```
setCustomFields({params})
```

##### params
| 参数名        | 类型 | 是否必填 | 描述                                                         |
| :------------ | :--- | :------- | :----------------------------------------------------------- |
| custom_fields | 数组 | 可选     | 工单自定义字段数组,每次提交工单时,都会将自定义字段添加到工单中 |

##### 示例代码
```javascript
import kf5sdk from 'react-native-kf5sdk';

var params = {
	custom_fields : [
		{
			"name" : "fields_123",
			"value" : "iOS"
		}, 
		{
			"name" : "fields_321",
			"value" : "测试应用"
		}
	]
};
kf5sdk.setCustomFields(params);  
```

##### 补充说明  

使用此接口，需配合工单模块一起使用。工单自定义字段的key在kf5后台的设置工单自定义字段里获取。 

#### * setTopBarColor

设置头部nav的颜色样式  
```
setTopBarColor({params})  
```

##### params
| 参数名            | 类型   | 是否必填 | 描述                                                 |
| :---------------- | :----- | :------- | :--------------------------------------------------- |
| navColor          | 字符串 | 可选     | 头部nav背景颜色，默认值：#3E4245                     |
| textColor         | 字符串 | 可选     | 头部nav的TextView字体颜色，默认值：#FFFFFF           |
| centerTextSize    | 整型   | 可选     | 头部Nav中间Textview字体大小，默认值：22              |
| rightTextSize     | 整型   | 可选     | 头部Nav右侧TextView字体大小，iOS不可用，默认值：20   |
| centerTextVisible | 布尔型 | 可选     | 头部Nav中间TextView是否可见，iOS不可用，默认值：true |
| rightTextVisible  | 布尔型 | 可选     | 头部Nav右侧TextView是否可见，默认值：true            |

##### 示例代码
```
import kf5sdk from 'react-native-kf5sdk';

var params = {
    navColor: "#3E4245",
    textColor: "#FFFFFF",
    centerTextSize: 22,
    rightTextSize: 20,
    centerTextVisible: true,
    rightTextVisible: true,
};
kf5sdk.setTopBarColor(params);
```

##### 补充说明  

使用此接口，需配合其他接口一起使用。 

## 四、推送设置

云客服移动SDK 通知普通用户工单被客服公开回复，或即时交谈的离线消息。

目前集成推送通知的唯一方法是使用一个回调的API，在使用该API前，您必须保证您的应用开启了远程推送服务。

在这里，你需要做三件事：
1、  在你的账号里配置您的移动SDK应用程序启动推送通知
2、  设置回调URL支持我们的回调
3、  在您的应用程序处理用户的唯一标示和推送请求
![img](http://developer.kf5.com/images/sdk/ios-sdk-push-1.png)
#### 回调的API
当有消息发送时，云客服SDK将通知您设置的回调URL。之后，您的服务必须处理发送推送通知到最终用户的设备。
当我们想要发送一个通知给最终用户,我们会发送一个流请求的URI设置应用程序配置。示例请求:

```ruby
 <回调URL>
Content-Type: application/json
Accept: application/json
{
  // 用户的信息 object
  "user":{
      // 被推送的user_id inter
      "user_id":123
    },
  // 设备的列表 array
  "device":[{ 
      // 设备的唯一标示 string
      "device_token":"1234567890",
      // 设备的类型 string
      "type":"IOS"
  }],
  // 推送通知信息 object
  "notification": {
    // 工单的id
    "ticket_id":"123",
    // 类型
    "type":"ticket"
  }
}
```
同样，在聊天中发送离线消息中，向设置的回调URL发送的数据示例:
```ruby
 <回调URL>
Content-Type: application/json
Accept: application/json
{
  // 用户的信息 object
  "user":{
      // 被推送的user_id inter
      "user_id":123
    },
  // 设备的列表 array
  "device":[{
      // 设备的唯一标示 string
      "device_token":"1234567890",
      // 设备的类型 string
       "type":"IOS"
  }],
  // 推送通知信息 object
  "notification": {
      // 聊天消息
      "message":"hello",
      // 类型
      "type":"im"
  }
}
```