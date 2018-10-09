package com.kf5.rn;

/**
 * @author Chosen
 * @create 2018/9/28 17:09
 * @email 812219713@qq.com
 */
public class ChatMetadataManager {

    private static String metadata;

    public static String getMetadata() {
        return metadata;
    }

    public static void setMetadata(String metadata) {
        ChatMetadataManager.metadata = metadata;
    }
}
