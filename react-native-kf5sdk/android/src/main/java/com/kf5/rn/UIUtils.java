package com.kf5.rn;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * @author Chosen
 * @create 2018/9/28 15:26
 * @email 812219713@qq.com
 */
public class UIUtils {

    /**
     * 设置Activity头部的相关属性
     *
     * @param topLayout
     *            父控件
     * @param backImage
     *            返回按钮
     * @param tvTitle
     *            标题
     * @param tvRight
     *            右侧按钮
     */
    public static void setNavBarUI(View topLayout, ImageView backImage, TextView tvTitle, TextView tvRight) {

        try {
            ActivityNavBarConfig navBarConfig = KF5SDKActivityUIConfig.getActivityNavBarConfig();
            Context context = topLayout.getContext();
            if (navBarConfig == null)
                return;
            if (tvTitle != null) {

                if (navBarConfig.isCenterTextViewVisible()) {
                    if (!tvTitle.isShown()) {
                        tvTitle.setVisibility(View.VISIBLE);
                    }
                    tvTitle.setTextSize(navBarConfig.getCenterTextSize());
                    tvTitle.setTextColor(navBarConfig.getTextColor());
                } else {
                    if (tvTitle.isShown()) {
                        tvTitle.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (tvRight != null) {

                if (navBarConfig.isRightTextViewVisible()) {
                    if (!tvRight.isShown()) {
                        tvRight.setVisibility(View.VISIBLE);
                    }
                    tvRight.setTextSize(navBarConfig.getRightTextSize());
                    tvRight.setTextColor(navBarConfig.getTextColor());
                } else {
                    if (tvRight.isShown()) {
                        tvRight.setVisibility(View.INVISIBLE);
                    }

                }
            }

            if (backImage != null) {
                if (!TextUtils.isEmpty(navBarConfig.getBackImageSrc())) {
                    File file = new File(navBarConfig.getBackImageSrc());
                    if (file.exists()) {
                        backImage.setImageBitmap(BitmapFactory.decodeFile(navBarConfig.getBackImageSrc()));
                    }
                }
            }
            if (topLayout != null) {
                topLayout.setBackgroundColor(navBarConfig.getNavBgColor());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

}
