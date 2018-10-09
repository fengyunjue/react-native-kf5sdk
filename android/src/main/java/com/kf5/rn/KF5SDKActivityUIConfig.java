package com.kf5.rn;

/**
 * @author Chosen
 * @create 2018/9/28 15:05
 * @email 812219713@qq.com
 */
public class KF5SDKActivityUIConfig {

    private static ActivityNavBarConfig activityNavBarConfig;

    public static ActivityNavBarConfig getActivityNavBarConfig() {
        return activityNavBarConfig;
    }

    public static void setActivityNavBarConfig(ActivityNavBarConfig activityNavBarConfig) {
        KF5SDKActivityUIConfig.activityNavBarConfig = activityNavBarConfig;
    }

}
