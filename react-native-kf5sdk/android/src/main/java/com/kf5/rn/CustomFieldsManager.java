package com.kf5.rn;

/**
 * @author Chosen
 * @create 2018/9/28 15:12
 * @email 812219713@qq.com
 */
public class CustomFieldsManager {

    private CustomFieldsManager() {
    }

    private static String customFieldContent;

    public static void setCustomFieldContent(String jsonArrayContent) {
        customFieldContent = jsonArrayContent;
    }

    public static String getCustomFieldContent() {
        return customFieldContent;
    }
}
