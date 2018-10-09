/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';

import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NativeModules,
  TouchableHighlight,
  Alert
} from 'react-native';

const kf5sdk = NativeModules.KF5SDK;
const HelloWorld = NativeModules.HelloWorld;


export default class kf5demo extends Component {
  _onPressButton() { 
    Alert.alert(
        `你点击了按钮`,
        'Hello World！',
        [
            {text: '以后再说', onPress: () => console.log('Ask me later pressed')},
            {text: '取消', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
            {text: '确定', onPress: () => console.log('OK Pressed')},
        ]
    )
 }

 _callBack(result){
  Alert.alert(result.message);
}

 _init(){
  var param = {
    appId               : "00156f280e4a50995ffc932255cfaf55511e17a13afc6751",
    hostName            : "https://fecheng.kf5.com",
    appName             : "测试公司",
    email               : "11497946@qq.com",
    userName            : "test",
    phone               : "12345678901",
    verifyUserType      : 2,
    deviceToken         : "1234567890"
  };
  kf5sdk.initKF5(param,this._callBack);

  var param1 = {
    navColor            :"#00bffd",
    textColor           :"#FFFFFF",
    centerTextSize      : 22,
    rightTextSize       : 20,
    centerTextVisible   : true,
    rightTextVisible    : false
  };
  kf5sdk.setTopBarColor(param1);
        
  var param2 = {
    custom_fields:[
       {
         "name":"field_11175",
         "value":"12345678901"
       },
       {
         "name":"field_1003031",
         "value":"骑车"
       }
    ]
  };
  kf5sdk.setCustomFields(param2);
 }

 _showDoc(){
  var param = {
      type: 2,
  };
  kf5sdk.showHelpCenter(param);
 }

 _createTicket(){
  kf5sdk.showRequestCreation();
 }

 _showTicketList(){
  kf5sdk.showRequestList();
 }

 _showChat(){
  var metadata = [
      {
        "name":"来源",
        "value":"设置页面"
      },
      {
        "name":"应用名称",
        "value":"测试应用"
      }
    ];
                  
  var param = {
    metadata: metadata
  };
  kf5sdk.showChatView(param);

  kf5sdk.noAgentAlertActionBlock(()=>{
    Alert.alert("当前没有在线客服");
  })
 }

 render() {
     this._init();
     return (
       <View style={styles.container}>
         <View style={styles.buttonContainer}>
           <TouchableHighlight style={styles.button}
            underlayColor='green'
             onPress={this._showDoc}>
             <Text style={styles.text}>查看文档</Text>
           </TouchableHighlight>
         </View>
         <View style={styles.buttonContainer}>
           <TouchableHighlight style={styles.button}
            underlayColor='green'
             onPress={this._createTicket}>
               <Text style={styles.text}>创建工单</Text>
            </TouchableHighlight>
         </View>
         <View style={styles.buttonContainer}>
           <TouchableHighlight style={styles.button}
            underlayColor='green'
             onPress={this._showTicketList}>
               <Text style={styles.text}>查看工单列表</Text>
            </TouchableHighlight>
         </View>
         <View style={styles.buttonContainer}>
           <TouchableHighlight style={styles.button}
            underlayColor='green'
             onPress={this._showChat}>
               <Text style={styles.text}>IM对话</Text>
            </TouchableHighlight>
         </View>
       </View>
     );
   }
 }

 const styles = StyleSheet.create({
   container: {
    flex: 1,
    justifyContent: 'center',
   },
   buttonContainer: {
     margin: 30,
   },
   button:{
     padding: 10,
     borderColor: 'blue',
     borderWidth: 1,
     borderRadius: 5,
   },
   text:{
     textAlign:'center',
   }
 })


AppRegistry.registerComponent('kf5demo', () => kf5demo);
