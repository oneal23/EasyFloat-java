package com.yt.easyfloat.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.yt.easyfloat.enums.ShowPattern;
import com.yt.easyfloat.widget.appfloat.AppFloatManager;
import com.yt.easyfloat.widget.appfloat.FloatManager;

public class LifecycleUtils {
    private static int activityCount;

    public static void setLifecycleCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activity != null) {
                    ++activityCount;
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                checkShow(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity != null) {
                    --activityCount;
                    checkHide();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }


            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private static void checkShow(Activity activity) {
        if (activity != null) {
            if (FloatManager.getInstance().floatMap != null) {
                for (String tag : FloatManager.getInstance().floatMap.keySet()) {
                    AppFloatManager manager = FloatManager.getInstance().floatMap.get(tag);
                    if (manager != null && manager.getConfig() != null) {
                        if (manager.getConfig().showPattern == ShowPattern.BACKGROUND) {
                            setVisible(false, tag);
                        } else if (manager.getConfig().needShow) {
                            boolean isInfilerSet = false;
                            if (manager.getConfig().filterSet != null && manager.getConfig().filterSet.contains(activity.getComponentName().getClassName())) {
                                isInfilerSet = true;
                            }
                            setVisible(!isInfilerSet, tag);
                        }
                    }

                }
            }
        }
    }

    private static void checkHide() {
        if (!isForeground()) {
            for (String tag : FloatManager.getInstance().floatMap.keySet()) {
                AppFloatManager manager = FloatManager.getInstance().floatMap.get(tag);
                if (manager != null && manager.getConfig() != null) {
                    setVisible(manager.getConfig().showPattern != ShowPattern.FOREGROUND && manager.getConfig().needShow, tag);
                }
            }
        }
    }

    public static boolean isForeground() {
        return activityCount > 0;
    }

    private static void setVisible(boolean isShow, String tag) {
        FloatManager.visible(isShow, tag);
    }

}
