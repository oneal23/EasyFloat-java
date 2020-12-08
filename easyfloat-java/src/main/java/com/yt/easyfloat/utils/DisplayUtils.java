package com.yt.easyfloat.utils;

import android.app.Service;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.yt.easyfloat.permission.rom.RomUtils;

public class DisplayUtils {
    private final static String TAG = "DisplayUtils";

    public static int px2dp(Context context, float pxVal){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxVal / density + 0.5f);
    }

    public static int dp2px(Context context, float dpVal){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * density + 0.5f);
    }

    public static int px2sp(Context context, float pxValue){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽高
     */
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelOffset(resourceId);
        }
        return height;
    }

    public static int statusBarHeight(View view) {
        return getStatusBarHeight(view.getContext().getApplicationContext());
    }

    /**
     * 获取导航栏真实的高度（可能未显示）
     */
    public static int getNavigationBarHeight(Context context) {
        int height = 0;
        int resId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resId > 0) {
            height = context.getResources().getDimensionPixelOffset(resId);
        }
        return height;
    }

    /**
     * 获取导航栏当前的高度
     */
    public static int getNavigationBarCurrentHeight(Context context) {
        if (hasNavigationBar(context)){
            return getNavigationBarHeight(context);
        }
        return 0;
    }

    /**
     * 判断虚拟导航栏是否显示
     *
     * @param context 上下文对象
     * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
     */
    public static boolean hasNavigationBar(Context context) {
        if (getNavigationBarHeight(context) == 0) {
            return false;
        }

        if (RomUtils.checkIsHuaweiRom() && isHuaWeiHideNav(context)) {
            return false;
        }

        if (RomUtils.checkIsMiuiRom() && isMiuiFullScreen(context)) {
            return false;
        }

        if (RomUtils.checkIsVivoRom() && isVivoFullScreen(context)) {
             return false;
        }
        return isHasNavigationBar(context);
    }

    /**
     * 不包含导航栏的有效高度（没有导航栏，或者已去除导航栏的高度）
     */
    public static int rejectedNavHeight(Context context) {
        Point point = getScreenSize(context);
        if (point.x > point.y) {
            return point.y;
        }
        return point.y - getNavigationBarCurrentHeight(context);
    }

    /**
     * 华为手机是否隐藏了虚拟导航栏
     * @return true 表示隐藏了，false 表示未隐藏
     */
    private static boolean isHuaWeiHideNav(Context context) {
        int minH = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            minH = Settings.System.getInt(context.getContentResolver(), "navigationbar_is_min", 0);
        } else  {
            minH = Settings.Global.getInt(context.getContentResolver(), "navigationbar_is_min", 0);
        }
        return minH != 0;
    }

    /**
     * 小米手机是否开启手势操作
     * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
     */
    private static boolean isMiuiFullScreen(Context context) {
        int minH = Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0);
        return minH != 0;
    }

    /**
     * Vivo手机是否开启手势操作
     * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
     */
    private static boolean isVivoFullScreen(Context context) {
        int minH = Settings.Secure.getInt(context.getContentResolver(), "navigation_gesture_on", 0);
        return minH != 0;
    }

    /**
     * 其他手机根据屏幕真实高度与显示高度是否相同来判断
     */
    private static boolean isHasNavigationBar(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics realMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realMetrics);
        }
        int realWidth = realMetrics.widthPixels;
        int realHeight = realMetrics.heightPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        if (displayHeight + getNavigationBarHeight(context) > realHeight) {
            return false;
        }
        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0;
    }
}
