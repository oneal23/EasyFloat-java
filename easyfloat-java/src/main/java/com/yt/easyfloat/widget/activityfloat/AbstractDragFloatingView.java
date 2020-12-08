package com.yt.easyfloat.widget.activityfloat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yt.easyfloat.anim.AnimatorManager;
import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.utils.Logger;

public abstract class AbstractDragFloatingView extends FrameLayout {

    // 浮窗配置
    protected FloatConfig config;

    // 悬浮的父布局高度、宽度
    private int parentHeight = 0;
    private int parentWidth = 0;

    // 终点坐标
    private int lastX = 0;
    private int lastY = 0;

    // 浮窗各边距离父布局的距离
    private int leftDistance = 0;
    private int rightDistance = 0;
    private int topDistance = 0;
    private int bottomDistance = 0;
    private int minX = 0;
    private int minY = 0;
    private Rect parentRect = new Rect();
    private Rect floatRect = new Rect();
    private ViewGroup parentView = null;
    private boolean isCreated = false;

    public AbstractDragFloatingView(Context context) {
        super(context);
        init(context);
    }

    public AbstractDragFloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AbstractDragFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractDragFloatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    abstract int getLayoutId();

    abstract void renderView(View view);


    private void init(Context context) {
        config = new FloatConfig();
        initView(context);
        // 设置空点击事件，用于接收触摸事件
        setOnClickListener(null);
    }

    protected void initView(Context context) {
        if (getLayoutId() > 0) {
            View view = LayoutInflater.from(context).inflate(getLayoutId(), this);
            this.renderView(view);
            if (config != null && config.invokeView != null) {
                config.invokeView.invoke(this);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//    }
//
//    @SuppressLint("DrawAllocation")
//    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        super.onLayout(changed, l, t, r, b)

        // 初次显示，设置默认坐标、入场动画
        if (!isCreated) {
            isCreated = true;
            // 有固定坐标使用固定坐标，没有固定坐标设置偏移量
            if (!config.locationPair.equals(new Pair(0, 0))) {
                this.setX(config.locationPair.first);
                this.setY(config.locationPair.second);
            } else {
                this.setX(getX() + config.locationPair.first);
                this.setY(getY() + config.locationPair.second);
            }

            initParent();
            initDistanceValue();
            enterAnim();
        }
    }

    private void initParent() {
        if (getParent() != null && getParent() instanceof ViewGroup) {
            parentView = (ViewGroup) getParent();
            parentHeight = parentView.getHeight();
            parentWidth = parentView.getWidth();
            parentView.getGlobalVisibleRect(parentRect);
            Logger.e("parentRect:" + parentRect.toString());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event != null) {
            updateView(event);
        }
        // 是拖拽事件就进行拦截，反之不拦截
        // ps：拦截后将不再回调该方法，所以后续事件需要在onTouchEvent中回调
        return config.isDrag || super.onInterceptTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // updateView(event)是拖拽功能的具体实现
        if (event != null) {
            updateView(event);
        }
        // 如果是拖拽，这消费此事件，否则返回默认情况，防止影响子View事件的消费
        return config.isDrag || super.onTouchEvent(event);
    }

    /**
     * 更新位置信息
     */
    private void updateView(MotionEvent event) {

        if (config.callbacks != null) {
            config.callbacks.touchEvent(this, event);
        }
        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
            config.floatCallbacks.getBuilder().touchEvent(this, event);
        }

        // 关闭拖拽/执行动画阶段，不可拖动
        if (!config.dragEnable || config.isAnim) {
            config.isDrag = false;
            setPressed(true);
            return;
        }

        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 默认是点击事件，而非拖拽事件
                config.isDrag = false;
                setPressed(true);
                lastX = rawX;
                lastY = rawY;
                // 父布局不要拦截子布局的监听
                getParent().requestDisallowInterceptTouchEvent(true);
                initParent();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (parentHeight <= 0 || parentWidth <= 0) {
                    return;
                }

                int dx = rawX - lastX;
                int dy = rawY - lastY;
                // 忽略过小的移动，防止点击无效
                if (!config.isDrag && dx * dx + dy * dy < 81) {
                    return;
                }
                config.isDrag = true;

                float tempX = getX() + dx;
                float tempY = getY() + dy;
                // 检测是否到达边缘
                if (tempX < 0.0f) {
                    tempX = 0.0f;
                } else if (tempX > parentWidth - getWidth()) {
                    tempX = parentWidth - getWidth();
                }

                if (tempY < 0.0f) {
                    tempY = 0.0f;
                } else if (tempX > parentHeight - getHeight()) {
                    tempX = parentHeight - getHeight();
                }

                switch (config.sidePattern) {
                    case LEFT: {
                        tempX = 0.0f;
                        break;
                    }
                    case RIGHT: {
                        tempX = parentRect.right - getWidth();
                        break;
                    }
                    case TOP: {
                        tempY = 0.0f;
                        break;
                    }
                    case BOTTOM: {
                        tempY = parentRect.bottom - getHeight();
                        break;
                    }
                    case AUTO_HORIZONTAL: {
                        if (rawX * 2 - parentRect.left > parentRect.right) {
                            tempX = parentRect.right - getWidth();
                        } else {
                            tempX = 0.0f;
                        }
                        break;
                    }
                    case AUTO_VERTICAL: {
                        if (rawY - parentRect.top > parentRect.bottom - rawY) {
                            tempY = parentRect.bottom - getHeight();
                        } else {
                            tempY = 0.0f;
                        }
                        break;
                    }
                    case AUTO_SIDE: {
                        leftDistance = rawX - parentRect.left;
                        rightDistance = parentRect.right - rawX;
                        topDistance = rawY - parentRect.top;
                        bottomDistance = parentRect.bottom - rawY;

                        minX = Math.min(leftDistance, rightDistance);
                        minY = Math.min(topDistance, bottomDistance);

                        Pair<Float, Float> pair = sideForLatest(tempX, tempY);
                        tempX = pair.first;
                        tempY = pair.second;
                        break;
                    }
                    default: {
                        break;
                    }
                }
                setX(tempX);
                setY(tempY);
                lastX = rawX;
                lastY = rawY;
                if (config.callbacks != null) {
                    config.callbacks.drag(this, event);
                }
                if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                    config.floatCallbacks.getBuilder().drag(this, event);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                // 如果是拖动状态下即非点击按压事件
                setPressed(!config.isDrag);
                if (config.isDrag) {
                    switch (config.sidePattern) {
                        case RESULT_LEFT:
                        case RESULT_RIGHT:
                        case RESULT_TOP:
                        case RESULT_BOTTOM:
                        case RESULT_HORIZONTAL:
                        case RESULT_VERTICAL:
                        case RESULT_SIDE: {
                            sideAnim();
                        }
                        default: {
                            touchOver();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 拖拽结束或者吸附动画执行结束，更新配置
     */
    private void touchOver() {
        config.isAnim = false;
        config.isDrag = false;
        if (config.callbacks != null) {
            config.callbacks.dragEnd(this);
        }
        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
            config.floatCallbacks.getBuilder().dragEnd(this);
        }
    }

    /**
     * 拖拽结束，吸附屏幕边缘
     */
    private void sideAnim() {
        // 计算一些数据
        initDistanceValue();
        String animType = "translationX";
        float startValue = 0.0f;
        float endValue = 0.0f;
        switch (config.sidePattern) {
            case RESULT_LEFT: {
                animType = "translationX";
                startValue = getTranslationX();
                endValue = -leftDistance + getTranslationX();
                break;
            }
            case RESULT_RIGHT: {
                animType = "translationX";
                startValue = getTranslationX();
                endValue = rightDistance + getTranslationX();
                break;
            }
            case RESULT_HORIZONTAL: {
                animType = "translationX";
                startValue = getTranslationX();
                if (leftDistance < rightDistance) {
                    endValue = -leftDistance + getTranslationX();
                } else {
                    endValue = rightDistance + getTranslationX();
                }
                break;
            }
            case RESULT_TOP: {
                animType = "translationY";
                startValue = getTranslationY();
                endValue = -topDistance + getTranslationY();
                break;
            }
            case RESULT_BOTTOM: {
                animType = "translationY";
                startValue = getTranslationY();
                endValue = bottomDistance + getTranslationY();
                break;
            }
            case RESULT_VERTICAL: {
                animType = "translationY";
                startValue = getTranslationY();
                if (topDistance < bottomDistance) {
                    endValue = -topDistance + getTranslationY();
                } else {
                    endValue = bottomDistance + getTranslationY();
                }
                break;
            }
            case RESULT_SIDE: {
                if (minX < minY) {
                    animType = "translationX";
                    startValue = getTranslationX();
                    if (leftDistance < rightDistance) {
                        endValue = -leftDistance + getTranslationX();
                    } else {
                        endValue = rightDistance + getTranslationX();
                    }
                } else {
                    animType = "translationY";
                    startValue = getTranslationY();
                    if (topDistance < bottomDistance) {
                        endValue = -topDistance + getTranslationY();
                    } else {
                        endValue = bottomDistance + getTranslationY();
                    }
                }
            }
            default: {
                break;
            }
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, animType, startValue, endValue);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                config.isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                touchOver();
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

    /**
     * 吸附在距离最近的那个边
     */
    private Pair<Float, Float> sideForLatest(float x, float y) {
        float x1 = x;
        float y1 = y;
        if (minX < minY) {
            if (leftDistance == minX) {
                x1 = 0.0f;
            } else {
                x1 = parentWidth - getWidth();
            }
        } else {
            if (topDistance == minY) {
                y1 = 0.0f;
            } else {
                y1 = parentHeight - getHeight();
            }
        }
        return new Pair(x1, y1);
    }

    /**
     * 计算一些边界距离数据
     */
    private void initDistanceValue() {
        // 获取 floatingView 所显示的矩形
        getGlobalVisibleRect(floatRect);

        leftDistance = floatRect.left - parentRect.left;
        rightDistance = parentRect.right - floatRect.right;
        topDistance = floatRect.top - parentRect.top;
        bottomDistance = parentRect.bottom - floatRect.bottom;

        minX = Math.min(leftDistance, rightDistance);
        minY = Math.min(topDistance, bottomDistance);
        Logger.i("leftDistance=" + leftDistance + ",rightDistance=" + rightDistance + "topDistance=" + topDistance + ",bottomDistance=" + bottomDistance);
    }

    /**
     * 入场动画
     */
    public void enterAnim() {
        if (parentView == null) {
            return;
        }
        AnimatorManager manager = new AnimatorManager(config.floatAnimator, this, parentView, config.sidePattern);
        Animator animator = manager.enterAnim();
        if (animator != null) {
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    config.isAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    config.isAnim = false;
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
     * 退出动画
     */
    public void exitAnim() {
        // 正在执行动画，防止重复调用
        if (config.isAnim || parentView == null) {
            return;
        }
        AnimatorManager manager = new AnimatorManager(config.floatAnimator, this, parentView, config.sidePattern);

        Animator animator = manager.exitAnim();
        // 动画为空，直接移除浮窗视图
        if (animator == null) {
            parentView.removeView(this);
        } else {
            final View view = this;
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    config.isAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    config.isAnim = false;
                    if (parentView != null) {
                        parentView.removeView(view);
                    }
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (config.callbacks != null) {
            config.callbacks.dismiss();
        }

        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
            config.floatCallbacks.getBuilder().dismiss();
        }
    }
}
