package com.yt.easyfloat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Pair;
import android.view.View;

import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.enums.ShowPattern;
import com.yt.easyfloat.enums.SidePattern;
import com.yt.easyfloat.interfaces.FloatCallbacks;
import com.yt.easyfloat.interfaces.OnAppFloatAnimator;
import com.yt.easyfloat.interfaces.OnDisplayHeight;
import com.yt.easyfloat.interfaces.OnFloatAnimator;
import com.yt.easyfloat.interfaces.OnFloatCallbacks;
import com.yt.easyfloat.interfaces.OnInvokeView;
import com.yt.easyfloat.interfaces.OnPermissionResult;
import com.yt.easyfloat.permission.PermissionUtils;
import com.yt.easyfloat.utils.LifecycleUtils;
import com.yt.easyfloat.utils.Logger;
import com.yt.easyfloat.widget.activityfloat.ActivityFloatManager;
import com.yt.easyfloat.widget.appfloat.AppFloatManager;
import com.yt.easyfloat.widget.appfloat.FloatManager;

import java.lang.ref.WeakReference;
import java.util.Set;

public class EasyFloat {
    private boolean isDebug;
    private WeakReference<Activity> activityWr;
    private boolean isInitialized;

    private static volatile EasyFloat singleton;

    private EasyFloat() {

    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public static EasyFloat getInstance() {
        if (singleton == null) {
            synchronized (EasyFloat.class) {
                if (singleton == null) {
                    singleton = new EasyFloat();
                }
            }
        }
        return singleton;
    }

    public static void init(Application application, boolean isDebug) {
        getInstance().initEasyFloat(application, isDebug);
    }

    private void initEasyFloat(Application application, boolean isDebug) {
        this.isDebug = isDebug;
        Logger.setLogEnable(isDebug);
        isInitialized = true;
        LifecycleUtils.setLifecycleCallbacks(application);
    }

    public static Builder with(Context context) {
        if (context instanceof Activity) {
            getInstance().activityWr = new WeakReference<Activity>((Activity) context);
        }
        return new Builder(context);
    }

    public static void dismiss(Activity activity, String tag) {
        ActivityFloatManager manager = getInstance().manager(activity);
        if (manager != null) {
            manager.dismiss(tag);
        }
    }

    public static void dismiss(Activity activity) {
        dismiss(activity, null);
    }

    public static void hide(Activity activity, String tag) {
        ActivityFloatManager manager = getInstance().manager(activity);
        if (manager != null) {
            manager.setVisibility(tag, View.GONE);
        }
    }

    public static void hide(Activity activity) {
        hide(activity, null);
    }

    public static void show(Activity activity, String tag) {
        ActivityFloatManager manager = getInstance().manager(activity);
        if (manager != null) {
            manager.setVisibility(tag, View.VISIBLE);
        }
    }

    public static void show(Activity activity) {
      show(activity, null);
    }

    public void setDragEnable(boolean dragEnable) {
       setDragEnable(null, dragEnable, null);
    }

    public static void setDragEnable(Activity activity, boolean dragEnable, String tag) {
        ActivityFloatManager manager = getInstance().manager(activity);
        if (manager != null) {
            manager.setDragEnable(dragEnable, tag);
        }
    }

    public static boolean isShow(Activity activity, String tag) {
        ActivityFloatManager manager = getInstance().manager(activity);
        if (manager != null) {
            return manager.isShow(tag);
        }
        return false;
    }

    /**
     * 获取我们传入的浮窗View
     */
    public static View getFloatView(Activity activity, String tag) {
        ActivityFloatManager manager = getInstance().manager(activity);
        if (manager != null) {
            return manager.getFloatView(tag);
        }
        return null;
    }

    /**
     * 获取Activity浮窗管理类
     */
    private ActivityFloatManager manager(Activity activity) {
        if (activity != null) {
            return new ActivityFloatManager(activity);
        }
        if (activityWr != null && activityWr.get() != null) {
            return new ActivityFloatManager(activityWr.get());
        }
        return null;
    }

    // *************************** 以下系统浮窗的相关方法 ***************************

    /**
     * 关闭系统级浮窗
     */
    public static void dismissAppFloat(String tag) {
        FloatManager.dismiss(tag);
    }

    public static void dismissAppFloat() {
        FloatManager.dismiss(null);
    }

    /**
     * 隐藏系统浮窗
     */
    public static void hideAppFloat(String tag) {
        FloatManager.visible(false, tag, false);
    }
    public static void hideAppFloat() {
        hideAppFloat(null);
    }
    /**
     * 显示系统浮窗
     */
    public static void showAppFloat(String tag) {
        FloatManager.visible(true, tag, true);
    }

    public static void showAppFloat() {
        showAppFloat(null);
    }

    /**
     * 设置系统浮窗是否可拖拽，先获取浮窗的config，后修改相应属性
     */
    public static void appFloatDragEnable(boolean dragEnable, String tag) {
        FloatConfig config = getConfig(tag);
        if (config != null) {
            config.dragEnable = dragEnable;
        }
    }

    public static void appFloatDragEnable(boolean dragEnable) {
        appFloatDragEnable(dragEnable, null);
    }

    /**
     * 获取系统浮窗是否显示，通过浮窗的config，获取显示状态
     */
    public static boolean appFloatIsShow(String tag) {
        FloatConfig config = getConfig(tag);
        return config != null && config.isShow;
    }

    /**
     * 获取系统浮窗中，我们传入的View
     */
    public static View getAppFloatView(String tag) {
        FloatConfig config = getConfig(tag);
        if (config != null) {
            return config.layoutView;
        }
        return null;
    }

    public static View getAppFloatView() {
        return getAppFloatView(null);
    }


    /**
     * 以下几个方法为：系统浮窗过滤页面的添加、移除、清空
     */
    public static void filterActivity(Activity activity, String tag) {
        Set<String> set = getFilterSet(tag);
        if (set != null) {
            set.add(activity.getComponentName().getClassName());
        }
    }

    public static void filterActivities(String tag, Class... clazz) {
        Set<String> set = getFilterSet(tag);
        if (set != null && clazz.length > 0) {
            for (Class c : clazz) {
                set.add(c.getName());
            }
        }
    }

    public static void removeFilter(Activity activity, String tag) {
        Set<String> set = getFilterSet(tag);
        if (set != null) {
            set.remove(activity.getComponentName().getClassName());
        }
    }

    public static void removeFilters(String tag, Class... clazz) {
        Set<String> set = getFilterSet(tag);
        if (set != null && clazz.length > 0) {
            for (Class c : clazz) {
                set.remove(c.getName());
            }
        }
    }

    /**
     * 获取系统浮窗的config
     */
    private static FloatConfig getConfig(String tag) {
        AppFloatManager manager = FloatManager.getAppFloatManager(tag);
        if (manager != null) {
            return manager.getConfig();
        }
        return null;
    }

    private static Set<String> getFilterSet(String tag) {
        FloatConfig config = getConfig(tag);
        if (config != null) {
            return config.filterSet;
        }
        return null;
    }


    /**
     * 浮窗的属性构建类，支持链式调用
     */
    public static class Builder implements OnPermissionResult {
        private Context context;

        private FloatConfig config = new FloatConfig();

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setSidePattern(SidePattern sidePattern) {
            config.sidePattern = sidePattern;
            return this;
        }

        public Builder setShowPattern(ShowPattern showPattern) {
            config.showPattern = showPattern;
            return this;
        }

        public Builder setLayout(int layoutId, OnInvokeView invokeView) {
            config.layoutId = layoutId;
            config.invokeView = invokeView;
            return this;
        }

        public Builder setGravity(int gravity, int offsetX, int offsetY) {
            config.gravity = gravity;
            config.offsetPair = new Pair<Integer, Integer>(offsetX, offsetY);
            return this;
        }

        public Builder setLocation(int x, int y) {
            config.locationPair = new Pair<Integer, Integer>(x, y);
            return this;
        }

        public Builder setTag(String floatTag) {
            config.floatTag = floatTag;
            return this;
        }

        public Builder setDragEnable(boolean dragEnable) {
            config.dragEnable = dragEnable;
            return this;
        }

        /**
         * 该方法针对系统浮窗，单页面浮窗无需设置
         */
        public Builder hasEditText(boolean hasEditText) {
            config.hasEditText = hasEditText;
            return this;
        }

        //        @Deprecated("建议直接在 setLayout 设置详细布局")
        public Builder invokeView(OnInvokeView invokeView) {
            config.invokeView = invokeView;
            return this;
        }

        /**
         * 通过传统接口，进行浮窗的各种状态回调
         */
        public Builder registerCallbacks(OnFloatCallbacks callbacks) {
            config.callbacks = callbacks;
            return this;
        }

        /**
         * 针对kotlin 用户，传入带FloatCallbacks.Builder 返回值的 lambda，可按需回调
         * 为了避免方法重载时 出现编译错误的情况，更改了方法名
         */
        public Builder registerCallbacks(FloatCallbacks.Builder builder) {
            if (config.floatCallbacks == null) {
                config.floatCallbacks = new FloatCallbacks();
            }
            config.floatCallbacks.setBuilder(builder);
            return this;
        }

        public Builder setAnimator(OnFloatAnimator floatAnimator) {
            config.floatAnimator = floatAnimator;
            return this;
        }

        public Builder setAppFloatAnimator(OnAppFloatAnimator appFloatAnimator) {
            config.appFloatAnimator = appFloatAnimator;
            return this;
        }

        /**
         * 设置屏幕的有效显示高度（不包含虚拟导航栏的高度）
         */
        public Builder setDisplayHeight(OnDisplayHeight displayHeight) {
            config.displayHeight = displayHeight;
            return this;
        }

        public Builder setMatchParent(Boolean widthMatch, Boolean heightMatch) {
            config.widthMatch = widthMatch;
            config.heightMatch = heightMatch;
            return this;
        }

        /**
         * 设置需要过滤的Activity，仅对系统浮窗有效
         */
        public Builder setFilter(Class... clazz) {
            if (clazz != null && clazz.length > 0) {
                for (Class c : clazz) {
                    config.filterSet.add(c.getName());
                    // 过滤掉当前Activity
                    if (context != null && context instanceof Activity) {
                        Activity activity = (Activity) context;
                        if (c.getName() == activity.getComponentName().getClassName()) {
                            config.filterSelf = true;
                        }
                    }
                }
            }
            return this;
        }

        /**
         * 创建浮窗，包括Activity浮窗和系统浮窗，如若系统浮窗无权限，先进行权限申请
         */
        public void show() {
            if (config.layoutId <= 0) {
                callbackCreateFailed(EasyFloatMessage.WARN_NO_LAYOUT);
                return;
            }
            if (checkUninitialized()) {
                callbackCreateFailed(EasyFloatMessage.WARN_UNINITIALIZED);
                return;
            }
            if (config.showPattern == ShowPattern.CURRENT_ACTIVITY) {
                createActivityFloat();
            } else if (PermissionUtils.checkPermission(context)) {
                createAppFloat();
            } else {
                requestPermission();
            }
        }


        /**
         * 检测是否存在全局初始化的异常
         */
        private boolean checkUninitialized() {
            boolean isInit = false;
            switch (config.showPattern) {
                case CURRENT_ACTIVITY: {
                    isInit = false;
                    break;
                }
                case FOREGROUND:
                case BACKGROUND: {
                    isInit = !getInstance().isInitialized();
                    break;
                }
                case ALL_TIME: {
                    isInit = !config.filterSet.isEmpty() && !getInstance().isInitialized();
                    break;
                }
                default: {
                    break;
                }
            }
            return isInit;
        }

        /**
         * 通过Activity浮窗管理类，创建Activity浮窗
         */
        private void createActivityFloat() {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                new ActivityFloatManager(activity).createFloat(config);
            } else {
                callbackCreateFailed(EasyFloatMessage.WARN_CONTEXT_ACTIVITY);
            }
        }

        /**
         * 通过浮窗管理类创建系统浮窗
         */
        private void createAppFloat() {
            FloatManager.getInstance().create(context, config);
        }

        /**
         * 通过Fragment去申请系统悬浮窗权限
         */
        private void requestPermission() {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                PermissionUtils.requestPermission(activity, this);
            } else {
                callbackCreateFailed(EasyFloatMessage.WARN_CONTEXT_REQUEST);
            }
        }


        @Override
        public void permissionResult(boolean isOpen) {

        }

        /**
         * 回调创建失败
         */
        private void callbackCreateFailed(String reason) {
            if (config.callbacks != null) {
                config.callbacks.createdResult(false, reason, null);
            }
            if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                config.floatCallbacks.getBuilder().createdResult(false, reason, null);
            }
            Logger.w(reason);
            if (reason.equals(EasyFloatMessage.WARN_NO_LAYOUT) || reason.equals(EasyFloatMessage.WARN_UNINITIALIZED) || reason.equals(EasyFloatMessage.WARN_CONTEXT_ACTIVITY)) {
                // 针对无布局、未按需初始化、Activity浮窗上下文错误，直接抛异常
                try {
                    throw new Exception(reason);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
