package com.kf5.rn;

import android.support.v4.util.ArrayMap;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.kf5.sdk.system.init.UserInfoAPI;
import com.kf5.sdk.system.internet.HttpRequestCallBack;
import com.kf5.sdk.system.utils.SPUtils;
import com.kf5.sdk.system.utils.SafeJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Chosen
 * @create 2018/9/28 14:40
 * @email 812219713@qq.com
 */
public class SDKInitializeUtil {

    private static final String HOSTNAME = "hostName";

    private static final String APPID = "appId";

    private static final String EMAIL = "email";

    private static final String USERNAME = "userName";

    private static final String APPNAME = "appName";

    private static final String DEVICE_TOKEN = "deviceToken";

    private static final String PHONE = "phone";

    private static final String VERIFYUSERTYPE = "verifyUserType";

    private static final String ERROR_CODE = "code";

    private static final String MESSAGE = "message";

    private static final String ERROR = "error";

    private static final String USER_TOKEN = "userToken";

    private static final String NAME = "name";

    private static final String DEVICETOKENS = "deviceTokens";

    private static final String ANDROID = "ANDROID";

    private static final String TAG = "React-Native";

    private static final String SOURCE = "source";

    private static final int RESULT_OK = 0;

    public static void initSDK(final ReadableMap readableMap, Callback callback) {

        String hostName = RNUtils.optString(readableMap, HOSTNAME);
        String appid = RNUtils.optString(readableMap, APPID);
        String email = RNUtils.optString(readableMap, EMAIL);
        String userName = RNUtils.optString(readableMap, USERNAME);
        String appName = RNUtils.optString(readableMap, APPNAME);
        String deviceToken = RNUtils.optString(readableMap, DEVICE_TOKEN);
        String phone = RNUtils.optString(readableMap, PHONE);
        int userType = RNUtils.optInt(readableMap, VERIFYUSERTYPE);
        SPUtils.saveAppID(appid);
        SPUtils.saveHelpAddress(hostName);
        if (TextUtils.isEmpty(appName)) {
            SPUtils.saveTicketTitle("来自 android app 的工单请求");
        } else {
            SPUtils.saveTicketTitle("来自 " + appName + " 的工单请求");
        }
        boolean isPhone;
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(email)) {
            isPhone = userType == 2;
        } else {
            isPhone = !TextUtils.isEmpty(phone);
        }
        Map<String, String> updateMap = new ArrayMap<>();
        if (!TextUtils.isEmpty(email) && !TextUtils.equals("null", email)) {
            updateMap.put(EMAIL, email);
        }
        if (!TextUtils.isEmpty(phone) && !TextUtils.equals("null", phone)) {
            updateMap.put(PHONE, phone);
        }
        if (!TextUtils.isEmpty(userName) && !TextUtils.equals("null", userName)) {
            updateMap.put(NAME, userName);
        }
        // updateMap.put(DEVICE_TOKEN, deviceToken);
        // 验证手机
        if (isPhone) {
            final Map<String, String> map = new ArrayMap<>();
            map.put(PHONE, phone);
            map.put(SOURCE, TAG);
            login(deviceToken, map, updateMap, callback);
            // 验证邮箱
        } else {
            if (!TextUtils.isEmpty(email)) {
                Map<String, String> paramsMap = new ArrayMap<>();
                paramsMap.put(EMAIL, email);
                paramsMap.put(SOURCE, TAG);
                login(deviceToken, paramsMap, updateMap, callback);
            } else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ERROR_CODE, -1);
                    jsonObject.put(MESSAGE, "邮箱不能为空");
                    callback.invoke(jsonObject.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    private static void login(final String deviceToken, final Map<String, String> map,
                              final Map<String, String> updateMap, final Callback callback) {

        UserInfoAPI.getInstance().loginUser(map, new HttpRequestCallBack() {

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                printLog("登录用户成功" + result);
                dealLoginResult(result, deviceToken, map, updateMap, callback);

            }

            @Override
            public void onFailure(String result) {
                // TODO Auto-generated method stub
                printLog("登录用户失败" + result);
                dealFailureResult(result, callback);
            }
        });

    }

    private static void dealLoginResult(String result, String deviceToken, Map<String, String> map,
                                        Map<String, String> updateMap, Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            int code = SafeJson.safeInt(jsonObject, ERROR);
            if (code == RESULT_OK) {
                dealSuccessResult(result, deviceToken, updateMap, callback);
                printLog("登录成功");
            } else {
                createUser(deviceToken, updateMap, updateMap, callback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dealJsonExceptionResult(callback);
        }
    }

    private static void toggleBoolUpdateUserInfo(Map<String, String> updateMap, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject dataObj = SafeJson.safeObject(jsonObject, "data");
            JSONObject userObj = SafeJson.safeObject(dataObj, "user");
            String email = SafeJson.safeGet(userObj, EMAIL);
            String localEmail = updateMap.containsKey(EMAIL) ? updateMap.get(EMAIL) : "";
            String phone = SafeJson.safeGet(userObj, PHONE);
            String localPhone = updateMap.containsKey(PHONE) ? updateMap.get(PHONE) : "";
            String userName = SafeJson.safeGet(userObj, USERNAME);
            String localUserName = updateMap.containsKey(NAME) ? updateMap.get(NAME) : "";
            boolean needUpdate = false;
            Map<String, String> map = new ArrayMap<>();
            if (!TextUtils.isEmpty(localEmail) && !TextUtils.equals(localEmail, email)) {
                needUpdate = true;
                map.put(EMAIL, localEmail);
            }

            if (!TextUtils.isEmpty(localPhone) && !TextUtils.equals(localPhone, phone)) {
                needUpdate = true;
                map.put(PHONE, localPhone);
            }

            if (!TextUtils.isEmpty(localUserName) && !TextUtils.equals(localUserName, userName)) {
                needUpdate = true;
                map.put("name", localUserName);
            }

            if (needUpdate) {
                updateUserInfo(map);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void updateUserInfo(Map<String, String> updateMap) {

        printLog("更新用戶信息的內容" + updateMap.toString());

        UserInfoAPI.getInstance().updateUser(updateMap, new HttpRequestCallBack() {

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                printLog("更新用户信息成功" + result);
            }

            @Override
            public void onFailure(String result) {
                // TODO Auto-generated method stub

            }
        });
    }

    private static void createUser(final String deviceToken, final Map<String, String> paramsMap,
                                   final Map<String, String> updateMap, final Callback uzModuleContext) {

        UserInfoAPI.getInstance().createUser(paramsMap, new HttpRequestCallBack() {

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                printLog("创建用户成功" + result);
                dealCreateResult(result, deviceToken, updateMap, uzModuleContext);
            }

            @Override
            public void onFailure(String result) {
                // TODO Auto-generated method stub
                printLog("创建用户失败" + result);
                dealFailureResult(result, uzModuleContext);
            }
        });
    }

    private static void dealCreateResult(String result, String deviceToken, Map<String, String> updateMap,
                                         Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            int code = SafeJson.safeInt(jsonObject, ERROR);
            if (code == RESULT_OK) {
                dealSuccessResult(result, deviceToken, updateMap, callback);
                printLog("创建用户成功");
            } else {
                dealFailureResult(result, callback);
            }
        } catch (JSONException e) {
            dealJsonExceptionResult(callback);
        }
    }

    private static void dealSuccessResult(String result, String deviceToken, Map<String, String> updateMap,
                                          Callback callback) {

        try {
            printLog(result);
            JSONObject resultObj = new JSONObject(result);
            JSONObject dataObj = SafeJson.safeObject(resultObj, "data");
            JSONObject userObj = SafeJson.safeObject(dataObj, "user");
            SPUtils.saveUserToken(SafeJson.safeGet(userObj, USER_TOKEN));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ERROR_CODE, 0);
            jsonObject.put(MESSAGE, "初始化成功");
            callback.invoke(jsonObject.toString());
//            uzModuleContext.success(jsonObject, true);
            toggleBoolUpdateUserInfo(updateMap, result);
            toggleDeviceToken(deviceToken, userObj);
        } catch (JSONException e) {
            e.printStackTrace();
            dealJsonExceptionResult(callback);
        }

    }

    private static void toggleDeviceToken(String deviceToken, JSONObject dataObj) {
        if (!TextUtils.isEmpty(deviceToken) && !TextUtils.equals("null", deviceToken)) {
            JSONObject tokenObj = SafeJson.safeObject(dataObj, DEVICETOKENS);
            if (tokenObj != null) {
                JSONArray jsonArray = SafeJson.safeArray(tokenObj, ANDROID);
                int size = jsonArray.length();
                boolean saveToken = true;
                for (int i = 0; i < size; i++) {
                    try {
                        String itemToken = jsonArray.getString(i);
                        if (TextUtils.equals(deviceToken, itemToken)) {
                            saveToken = false;
                            break;
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (saveToken) {
                    Map<String, String> tokenMap = new ArrayMap<>();
                    tokenMap.put(DEVICE_TOKEN, deviceToken);
                    UserInfoAPI.getInstance().saveDeviceToken(tokenMap, new HttpRequestCallBack() {

                        @Override
                        public void onSuccess(String result) {
                            // TODO Auto-generated method stub
                            printLog("保存token成功" + result);
                        }

                        @Override
                        public void onFailure(String result) {
                            // TODO Auto-generated method stub
                            printLog("保存token失败" + result);
                        }
                    });
                }
            }

        }
    }

    private static void dealFailureResult(String result, Callback callback) {
        callback.invoke(result);
        printLog("登录失败回调");
    }

    private static void dealJsonExceptionResult(Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ERROR_CODE, -1);
            jsonObject.put(MESSAGE, "json数据格式异常");
            callback.invoke(jsonObject.toString());
            printLog("登录失败异常回调");
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private static void printLog(String msg) {
        Log.i(TAG, msg);
    }
}
