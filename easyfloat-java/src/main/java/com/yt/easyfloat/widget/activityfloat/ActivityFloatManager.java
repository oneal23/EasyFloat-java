package com.yt.easyfloat.widget.activityfloat;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;

import com.yt.easyfloat.data.FloatConfig;

public class ActivityFloatManager {

    private FrameLayout parentFrame;
    private Activity activity;

    public ActivityFloatManager(Activity activity) {
        this.activity = activity;
        View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        this.parentFrame = (FrameLayout) view;
    }

    /**
     * 创建Activity浮窗
     * 拖拽效果由自定义的拖拽布局实现；
     * 将拖拽布局，添加到Activity的根布局；
     * 再将浮窗的xml布局，添加到拖拽布局中，从而实现拖拽效果。
     */
    public void createFloat(FloatConfig config) {
        // 设置浮窗的拖拽外壳FloatingView
        FloatingView floatingView = new FloatingView(activity);
        String tag = getTag(config.floatTag);
        floatingView.setTag(tag);
        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        if (config.widthMatch) {
            width = FrameLayout.LayoutParams.MATCH_PARENT;
        }
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        if (config.heightMatch) {
            height = FrameLayout.LayoutParams.MATCH_PARENT;
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        if (config.locationPair.equals(new Pair<Integer, Integer>(0, 0))) {
            layoutParams.gravity = config.gravity;
        }
        floatingView.setLayoutParams(layoutParams);
        floatingView.setConfig(config);

        // 将FloatingView添加到根布局中
        parentFrame.addView(floatingView);

        // 设置Callbacks
        config.layoutView = floatingView;
        if (config.callbacks != null) {
            config.callbacks.createdResult(true, null, floatingView);
        }
        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
            config.floatCallbacks.getBuilder().createdResult(true, null, floatingView);
        }
    }

    /**
     * 关闭activity浮窗
     */
    public void dismiss(String tag) {
        FloatingView floatingView = floatingView(tag);
        if (floatingView != null) {
            floatingView.exitAnim();
        }

    }

    /**
     * 设置浮窗的可见性
     */
    public void setVisibility(String tag, int visibility) {

        FloatingView floatingView = floatingView(tag);
        if (floatingView != null) {
            floatingView.setVisibility(visibility);
            if (visibility == View.GONE) {
                if (floatingView.config.callbacks != null) {
                    floatingView.config.callbacks.hide(floatingView);
                }
                if (floatingView.config.floatCallbacks != null && floatingView.config.floatCallbacks.getBuilder() != null) {
                    floatingView.config.floatCallbacks.getBuilder().hide(floatingView);
                }
            } else {
                if (floatingView.config.callbacks != null) {
                    floatingView.config.callbacks.show(floatingView);
                }
                if (floatingView.config.floatCallbacks != null && floatingView.config.floatCallbacks.getBuilder() != null) {
                    floatingView.config.floatCallbacks.getBuilder().show(floatingView);
                }
            }
        }

    }

    /**
     * 获取浮窗是否显示
     */
    public boolean isShow(String tag) {
        if (tag != null) {
            FloatingView floatingView = floatingView(tag);
            if (floatingView != null) {
                return floatingView.getVisibility() == View.VISIBLE;
            }
        }
        return false;
    }

    /**
     * 设置是否可拖拽
     */
    public void setDragEnable(boolean dragEnable, String tag) {
        FloatingView floatingView = floatingView(tag);
        if (floatingView != null) {
            floatingView.config.dragEnable = dragEnable;
        }
    }

    /**
     * 获取我们传入的浮窗View
     */
    public View getFloatView(String tag) {
        return floatingView(tag);
    }

    /**
     * 获取浮窗的拖拽外壳FloatingView
     */
    private FloatingView floatingView(String tag) {
        String t = getTag(tag);
        return parentFrame.findViewWithTag(t);
    }

    /**
     * 如果未设置tag，使用类名作为tag
     */
    private String getTag(String tag) {
        if (tag == null) {
            return activity.getComponentName().getClassName();
        }
        return tag;
    }
}
