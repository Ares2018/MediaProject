package com.zjrb.daily.mediaselector.util;

/**
* 双击判断
* @author zhengy
* create at 2018/11/22 下午3:05
**/

public class DoubleUtils {
    /**
     * Prevent continuous click, jump two pages
     */
    private static long lastClickTime;
    private final static long TIME = 800;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
