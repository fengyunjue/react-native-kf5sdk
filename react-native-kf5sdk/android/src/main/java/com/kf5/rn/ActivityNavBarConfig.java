package com.kf5.rn;

import android.graphics.Color;

/**
 * @author Chosen
 * @create 2018/9/28 15:04
 * @email 812219713@qq.com
 */
public class ActivityNavBarConfig {

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getNavBgColor() {
        return navBgColor;
    }

    public void setNavBgColor(int navBgColor) {
        this.navBgColor = navBgColor;
    }

    public int getCenterTextSize() {
        return centerTextSize;
    }

    public void setCenterTextSize(int centerTextSize) {
        this.centerTextSize = centerTextSize;
    }

    public String getBackImageSrc() {
        return backImageSrc;
    }

    public void setBackImageSrc(String backImageSrc) {
        this.backImageSrc = backImageSrc;
    }

    public int getRightTextSize() {
        return rightTextSize;
    }

    public void setRightTextSize(int rightTextSize) {
        this.rightTextSize = rightTextSize;
    }


    private int textColor = Color.WHITE;

    private int navBgColor = Color.parseColor("#3E4245");

    private int centerTextSize = 22;

    private String backImageSrc;

    private int rightTextSize = 20;

    private boolean rightTextViewVisible = true;

    private boolean centerTextViewVisible = true;

    public boolean isCenterTextViewVisible() {
        return centerTextViewVisible;
    }

    public void setCenterTextViewVisible(boolean centerTextViewVisible) {
        this.centerTextViewVisible = centerTextViewVisible;
    }

    public boolean isRightTextViewVisible() {
        return rightTextViewVisible;
    }

    public void setRightTextViewVisible(boolean rightTextViewVisible) {
        this.rightTextViewVisible = rightTextViewVisible;
    }


}
