package com.kf5.rn;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.kf5.sdk.helpcenter.ui.HelpCenterActivity;
import com.kf5.sdk.helpcenter.ui.HelpCenterTypeActivity;
import com.kf5.sdk.helpcenter.ui.HelpCenterTypeChildActivity;
import com.kf5.sdk.im.ui.KF5ChatActivity;
import com.kf5.sdk.system.utils.LogUtil;
import com.kf5.sdk.system.utils.SPUtils;
import com.kf5.sdk.ticket.ui.FeedBackActivity;
import com.kf5.sdk.ticket.ui.LookFeedBackActivity;


/**
 * @author Chosen
 * @create 2018/9/28 14:35
 * @email 812219713@qq.com
 */
public class KF5SDKModule extends ReactContextBaseJavaModule {

    private static final String TYPE = "type";

    private static final String CENTERTEXTSIZE = "centerTextSize";

    private static final String BACKIMGSRC = "backImgSrc";

    private static final String RIGHTTEXTSIZE = "rightTextSize";

    private static final String TEXTCOLOR = "textColor";

    private static final String CENTERTEXTVISIBLE = "centerTextVisible";

    private static final String RIGHTTEXTVISIBLE = "rightTextVisible";

    private static final String NAVCOLOR = "navColor";

    private static final String CUSTOM_FIELDS = "custom_fields";

    private static final String METADATA = "metadata";

    private ReactContext reactContext;

    public KF5SDKModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        SPUtils.getInstance(reactContext);
        SPUtils.clearSP();
    }

    @Override
    public String getName() {
        return "KF5SDK";
    }


    @ReactMethod
    public void initKF5(ReadableMap readableMap, Callback callback) {
        SDKInitializeUtil.initSDK(readableMap, callback);
    }

    @ReactMethod
    public void showHelpCenter(ReadableMap readableMap) {
        int type = RNUtils.optInt(readableMap, TYPE);
        Intent intent = new Intent();
        switch (type) {
            case 0:
                intent.setClass(reactContext, HelpCenterActivity.class);
                break;
            case 1:
                intent.setClass(reactContext, HelpCenterActivity.class);
                break;
            case 2:
                intent.setClass(reactContext, HelpCenterTypeActivity.class);
                break;
            case 3:
                intent.setClass(reactContext, HelpCenterTypeChildActivity.class);
                break;
            default:
                intent.setClass(reactContext, HelpCenterActivity.class);
                break;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reactContext.startActivity(intent);
    }


    @ReactMethod
    public void showRequestCreation() {
        reactContext.startActivity(new Intent(reactContext, FeedBackActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    @ReactMethod
    public void showRequestList() {
        reactContext.startActivity(new Intent(reactContext, LookFeedBackActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ReactMethod
    public void showChatView(ReadableMap readableMap) {
        ReadableArray array = RNUtils.optArray(readableMap, METADATA);
        if (array != null && !TextUtils.isEmpty(array.toString())) {
            ChatMetadataManager.setMetadata(array.toString());
        }
        reactContext.startActivity(new Intent(reactContext, KF5ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ReactMethod
    public void setTopBarColor(ReadableMap readableMap) {
        String textColor = RNUtils.optString(readableMap, TEXTCOLOR);
        String topBarBackgroundColor = RNUtils.optString(readableMap, NAVCOLOR);
        int tvCenterTextSize = RNUtils.optInt(readableMap, CENTERTEXTSIZE);
        String backImgSrc = RNUtils.optString(readableMap, BACKIMGSRC);
        int tvRightTextSize = RNUtils.optInt(readableMap, RIGHTTEXTSIZE);
        boolean titleTextViewVisible = RNUtils.optBoolean(readableMap, CENTERTEXTVISIBLE, true);
        boolean rightTextViewVisible = RNUtils.optBoolean(readableMap, RIGHTTEXTVISIBLE, true);

        ActivityNavBarConfig activityNavBarConfig = new ActivityNavBarConfig();
        activityNavBarConfig.setBackImageSrc(backImgSrc);
        activityNavBarConfig.setTextColor(Color.parseColor(textColor));
        activityNavBarConfig.setCenterTextSize(tvCenterTextSize);
        activityNavBarConfig.setNavBgColor(Color.parseColor(topBarBackgroundColor));
        activityNavBarConfig.setRightTextSize(tvRightTextSize);
        activityNavBarConfig.setRightTextViewVisible(rightTextViewVisible);
        activityNavBarConfig.setCenterTextViewVisible(titleTextViewVisible);
        KF5SDKActivityUIConfig.setActivityNavBarConfig(activityNavBarConfig);
    }


    @ReactMethod
    public void setCustomFields(ReadableMap readableMap) {
        LogUtil.printf("设置工单自定义字段");
        String customFields = RNUtils.optString(readableMap, CUSTOM_FIELDS);
        CustomFieldsManager.setCustomFieldContent(customFields);
    }
}
