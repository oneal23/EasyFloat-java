package com.yt.easyfloat.widget.appfloat;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.yt.easyfloat.anim.AppFloatAnimatorManager;
import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.enums.ShowPattern;
import com.yt.easyfloat.interfaces.OnFloatTouchListener;
import com.yt.easyfloat.utils.DisplayUtils;
import com.yt.easyfloat.utils.LifecycleUtils;
import com.yt.easyfloat.utils.Logger;

public class AppFloatManager {

    public WindowManager windowManager;

    public WindowManager.LayoutParams params;

    public ParentFrameLayout frameLayout;

    private TouchUtils touchUtils;

    private Context context;

    private FloatConfig config;

    public FloatConfig getConfig() {
        return config;
    }

    public AppFloatManager(Context context, FloatConfig config) {
        this.context = context;
        this.config = config;
    }

    public void createFloat() {
        try {
            touchUtils = new TouchUtils(context, config);
            initParams();
            addView();
            config.isShow = true;
        } catch (Exception e) {
            if (config.callbacks != null) {
                config.callbacks.createdResult(false, e.getMessage(), null);
            }

            if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                config.floatCallbacks.getBuilder().createdResult(false, e.getMessage(), null);
            }
        }

    }

    private void initParams() {
        windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);

        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(type);
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.START | Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (config.widthMatch) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        if (config.heightMatch) {
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        if (!config.locationPair.equals(new Pair<Integer, Integer>(0, 0))) {
            params.x = config.locationPair.first;
            params.y = config.locationPair.second;
        }
    }

    /**
     * 将自定义的布局，作为xml布局的父布局，添加到windowManager中，
     * 重写自定义布局的touch事件，实现拖拽效果。
     */
    private void addView() {
        // 创建一个frameLayout作为浮窗布局的父容器
//        frameLayout = new ParentFrameLayout(context, config);
        frameLayout = new ParentFrameLayout(context);
        frameLayout.setConfig(config);
        frameLayout.setTag(config.floatTag);

        // 将浮窗布局文件添加到父容器frameLayout中，并返回该浮窗文件
        final View floatingView = LayoutInflater.from(context).inflate(config.layoutId, frameLayout, true);
        // 为了避免创建的时候闪一下，我们先隐藏视图，不能直接设置GONE，否则定位会出现问题
        floatingView.setVisibility(View.INVISIBLE);
        // 将frameLayout添加到系统windowManager中
        windowManager.addView(frameLayout, params);
        // 通过重写frameLayout的Touch事件，实现拖拽效果
        frameLayout.setTouchListener(new OnFloatTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                touchUtils.updateFloat(frameLayout, event, windowManager, params);
            }
        });

        frameLayout.setLayoutListener(new ParentFrameLayout.OnLayoutListener() {

            @Override
            public void onLayout() {
                setGravity(frameLayout);
                if (config.filterSelf
                        || (config.showPattern == ShowPattern.FOREGROUND && !LifecycleUtils.isForeground())
                        || (config.showPattern == ShowPattern.BACKGROUND && LifecycleUtils.isForeground())) {
                    setVisible(View.GONE, true);
                } else {
                    enterAnim(floatingView);
                }
                config.layoutView = floatingView;
                if (config.invokeView != null) {
                    config.invokeView.invoke(floatingView);
                }

                if (config.callbacks != null) {
                    config.callbacks.createdResult(true, null, floatingView);
                }

                if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                    config.floatCallbacks.getBuilder().createdResult(true, null, floatingView);
                }
            }
        });
    }

    /**
     * 设置浮窗的对齐方式，支持上下左右、居中、上中、下中、左中和右中，默认左上角
     * 支持手动设置的偏移量
     */
    @SuppressLint("RtlHardcoded")
    private void setGravity(View view) {
        if (!config.locationPair.equals(new Pair<Integer, Integer>(0, 0)) || view == null) {
            return;
        }
        Rect parentRect = new Rect();
        // 获取浮窗所在的矩形
        windowManager.getDefaultDisplay().getRectSize(parentRect);
        int parentBottom = parentRect.bottom - DisplayUtils.statusBarHeight(view);
        switch (config.gravity) {
            // 右上
            case Gravity.END:
            case (Gravity.END | Gravity.TOP):
            case Gravity.RIGHT:
            case (Gravity.RIGHT | Gravity.TOP): {
                params.x = parentRect.right - view.getWidth();
                break;
            }
            // 左下
            case (Gravity.START | Gravity.BOTTOM):
            case Gravity.BOTTOM:
            case (Gravity.LEFT | Gravity.BOTTOM): {
                params.y = parentBottom - view.getHeight();
                break;
            }
            // 右下
            case (Gravity.END | Gravity.BOTTOM):
            case (Gravity.RIGHT | Gravity.BOTTOM): {
                params.x = parentRect.right - view.getWidth();
                params.y = parentBottom - view.getHeight();
                break;
            }
            // 居中
            case Gravity.CENTER: {
                params.x = (parentRect.right - view.getWidth()) / 2;
                params.y = (parentBottom - view.getHeight()) / 2;
                break;
            }
            // 上中
            case Gravity.CENTER_HORIZONTAL:
            case (Gravity.TOP | Gravity.CENTER_HORIZONTAL): {
                params.x = (parentRect.right - view.getWidth()) / 2;
                break;
            }
            // 下中
            case (Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL): {
                params.x = (parentRect.right - view.getWidth()) / 2;
                params.y = parentBottom - view.getHeight();
                break;
            }
            // 左中
            case Gravity.CENTER_VERTICAL:
            case (Gravity.START | Gravity.CENTER_VERTICAL):
            case (Gravity.LEFT | Gravity.CENTER_VERTICAL): {
                params.y = parentBottom - view.getHeight() / 2;
                break;
            }
            // 右中
            case (Gravity.END | Gravity.CENTER_VERTICAL):
            case (Gravity.RIGHT | Gravity.CENTER_VERTICAL): {
                params.x = parentRect.right - view.getWidth();
                params.y = parentBottom - view.getHeight() / 2;
                break;
            }
            // 其他情况，均视为左上
            default: {
                break;
            }

        }
        // 设置偏移量
        params.x += config.offsetPair.first;
        params.y += config.offsetPair.second;
        // 更新浮窗位置信息
        windowManager.updateViewLayout(view, params);
    }

    /**
     * 设置浮窗的可见性
     */
    public void setVisible(int visible, boolean needShow) {
        if (frameLayout == null || frameLayout.getChildCount() < 1) {
            return;
        }
        frameLayout.setVisibility(visible);
        View view = frameLayout.getChildAt(0);
        if (visible == View.VISIBLE) {
            config.isShow = true;
            if (config.callbacks != null) {
                config.callbacks.show(view);
            }
            if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                config.floatCallbacks.getBuilder().show(view);
            }
        } else {
            config.isShow = false;
            if (config.callbacks != null) {
                config.callbacks.hide(view);
            }
            if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                config.floatCallbacks.getBuilder().hide(view);
            }
        }
    }

    /**
     * 入场动画
     */
    private void enterAnim(final View floatingView) {
        if (frameLayout == null || config.isAnim) {
            return;
        }
        AppFloatAnimatorManager manager = new AppFloatAnimatorManager(frameLayout, params, windowManager, config);
        ValueAnimator animator = manager.enterAnim();
        if (animator != null) {
            // 可以延伸到屏幕外，动画结束需要去除该属性，不然旋转屏幕可能置于屏幕外部
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    floatingView.setVisibility(View.VISIBLE);
                    config.isAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    config.isAnim = false;
                    // 不需要延伸到屏幕外了，防止屏幕旋转的时候，浮窗处于屏幕外
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        } else {
            floatingView.setVisibility(View.VISIBLE);
            windowManager.updateViewLayout(floatingView, params);
        }
    }

    /**
     * 退出动画
     */
    public void exitAnim() {
        if (frameLayout == null || config.isAnim) {
            return;
        }
        AppFloatAnimatorManager manager = new AppFloatAnimatorManager(frameLayout, params, windowManager, config);
        ValueAnimator animator = manager.exitAnim();
        if (animator == null) {
            floatOver();
        } else {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    config.isAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    floatOver();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }

    /**
     * 退出动画执行结束/没有退出动画，进行回调、移除等操作
     */

    private void floatOver() {
        try {
            config.isAnim = false;
            FloatManager.remove(config.floatTag);
            windowManager.removeView(frameLayout);
        } catch (Exception e) {
            Logger.e("floatOver error=" + e.getMessage());
        }
    }

}
