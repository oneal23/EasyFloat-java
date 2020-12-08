package com.yt.easyfloat.widget.appfloat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.yt.easyfloat.EasyFloatMessage;
import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.utils.Logger;

import java.util.HashMap;
import java.util.Map;

public class FloatManager {
    private final static String DEFAULT_TAG = "default";

    public Map<String, AppFloatManager> floatMap;

    private static volatile FloatManager singleton;

    private FloatManager() {
        floatMap = new HashMap<String, AppFloatManager>();
    }

    public static FloatManager getInstance() {
        if (singleton == null) {
            synchronized (FloatManager.class) {
                if (singleton == null) {
                    singleton = new FloatManager();
                }
            }
        }
        return singleton;
    }

    public void create(Context context, FloatConfig config) {
        if (checkTag(config)) {
            AppFloatManager appFloatManager = new AppFloatManager(context, config);
            appFloatManager.createFloat();
            floatMap.put(config.floatTag, appFloatManager);
        } else {
            if (config.callbacks != null) {
                config.callbacks.createdResult(false, EasyFloatMessage.WARN_REPEATED_TAG, null);
            }
            Logger.w(EasyFloatMessage.WARN_REPEATED_TAG);
        }
    }

    /**
     * 设置浮窗的显隐，用户主动调用隐藏时，needShow需要为false
     */
    public static void visible(boolean isShow, String tag, boolean needShow) {
        AppFloatManager manager = getAppFloatManager(tag);
        if (manager != null) {
            manager.setVisible(isShow ? View.VISIBLE : View.GONE, needShow);
        }
    }

    public static void visible(boolean isShow, String tag) {
        AppFloatManager manager = getAppFloatManager(tag);
        if (manager != null) {
            manager.setVisible(isShow ? View.VISIBLE : View.GONE, true);
        }
    }

    /**
     * 关闭浮窗，执行浮窗的退出动画
     */
    public static void dismiss(String tag) {
        AppFloatManager manager = getAppFloatManager(tag);
        if (manager != null) {
            manager.exitAnim();
        }
    }

    /**
     * 移除当条浮窗信息，在退出完成后调用
     */
    public static void remove(String floatTag) {
        if (!TextUtils.isEmpty(floatTag)) {
            getInstance().floatMap.remove(floatTag);
        }
    }

    /**
     * 获取浮窗tag，为空则使用默认值
     */
    public static String getTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        return DEFAULT_TAG;
    }

    /**
     * 获取具体的系统浮窗管理类
     */
    public static AppFloatManager getAppFloatManager(String tag){
       return getInstance().floatMap.get(getTag(tag));
    }

    /**
     * 检测浮窗的tag是否有效，不同的浮窗必须设置不同的tag
     */
    private boolean checkTag(FloatConfig config) {
        // 如果未设置tag，设置默认tag
        config.floatTag = getTag(config.floatTag);
        return !floatMap.containsKey(config.floatTag);
    }
}