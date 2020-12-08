package com.yt.easyfloat.widget.appfloat;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.utils.DisplayUtils;

public class TouchUtils {

    // 窗口所在的矩形
    private Rect parentRect = new Rect();

    // 悬浮的父布局高度、宽度
    private int parentHeight = 0;
    private int parentWidth = 0;

    // 起点坐标
    private float lastX = 0f;
    private float lastY = 0f;

    // 浮窗各边距离父布局的距离
    private int leftDistance = 0;
    private int rightDistance = 0;
    private int topDistance = 0;
    private int bottomDistance = 0;

    // x轴、y轴的最小距离值
    private int minX = 0;
    private int minY = 0;
    private int[] location = new int[2];

    // 屏幕可用高度 - 浮窗自身高度 的剩余高度
    private int emptyHeight = 0;

    // 是否包含状态栏
    private boolean hasStatusBar = true;

    private Context context;
    private FloatConfig config;

    public TouchUtils(Context context, FloatConfig config) {
        this.context = context;
        this.config = config;
    }

    /**
     * 根据吸附模式，实现相应的拖拽效果
     */
    public void updateFloat(View view, MotionEvent event, WindowManager windowManager, WindowManager.LayoutParams params) {
        if (config.callbacks != null) {
            config.callbacks.touchEvent(view, event);
        }

        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
            config.floatCallbacks.getBuilder().touchEvent(view, event);
        }

        if (!config.dragEnable || config.isAnim) {
            config.isDrag = false;
            return;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                config.isDrag = false;
                // 记录触摸点的位置
                lastX = event.getRawX();
                lastY = event.getRawY();
                // 屏幕宽高需要每次获取，可能会有屏幕旋转、虚拟导航栏的状态变化
                parentWidth = DisplayUtils.getScreenWidth(context);
                parentHeight = config.displayHeight.getDisplayRealHeight(context);
                // 获取在整个屏幕内的绝对坐标
                view.getLocationOnScreen(location);
                // 通过绝对高度和相对高度比较，判断包含顶部状态栏
                hasStatusBar = location[1] > params.y;
                emptyHeight = parentHeight - view.getHeight();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // 移动值 = 本次触摸值 - 上次触摸值
                float dx = event.getRawX() - lastX;
                float dy = event.getRawY() - lastY;
                // 忽略过小的移动，防止点击无效
                if (!config.isDrag && dx * dx + dy * dy < 81) {
                    return;
                }
                config.isDrag = true;

                int x = (int) (params.x + dx);
                int y = (int) (params.y + dy);
                // 检测浮窗是否到达边缘
                if (x < 0) {
                    x = 0;
                } else if (x > parentWidth - view.getWidth()) {
                    x = parentWidth - view.getWidth();
                }

                if (y < 0) {
                    y = 0;
                } else if (y > emptyHeight - statusBarHeight(view) && hasStatusBar) {

                    y = emptyHeight - statusBarHeight(view);
                }
                switch (config.sidePattern) {
                    case LEFT: {
                        x = 0;
                        break;
                    }
                    case RIGHT: {
                        x = parentWidth - view.getWidth();
                        break;
                    }
                    case TOP: {
                        y = 0;
                        break;
                    }
                    case BOTTOM: {
                        y = parentHeight - view.getHeight();
                        break;
                    }
                    case AUTO_HORIZONTAL: {
                        if (event.getRawX() * 2 > parentWidth) {
                            x = parentWidth - view.getWidth();
                        } else {
                            x = 0;
                        }
                        break;
                    }
                    case AUTO_VERTICAL: {
                        if ((event.getRawY() - parentRect.top) * 2 > parentHeight) {
                            y = parentHeight - view.getHeight();
                        } else {
                            y = 0;
                        }
                        break;
                    }
                    case AUTO_SIDE: {
                        leftDistance = (int) event.getRawX();
                        rightDistance = (int) (parentWidth - event.getRawX());
                        topDistance = (int) (event.getRawY() - parentRect.top);
                        bottomDistance = (int) (parentHeight + parentRect.top - event.getRawY());

                        minX = Math.min(leftDistance, rightDistance);
                        minY = Math.min(topDistance, bottomDistance);
                        if (minX < minY) {
                            if (leftDistance == minX) {
                                x = 0;
                            } else {
                                x = parentWidth - view.getWidth();
                            }
                        } else {
                            if (topDistance == minY) {
                                y = 0;
                            } else {
                                y = parentHeight - view.getHeight();
                            }
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }

                // 重新设置坐标信息
                params.x = x;
                params.y = y;
                windowManager.updateViewLayout(view, params);
                if (config.callbacks != null) {
                    config.callbacks.drag(view, event);
                }
                if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                    config.floatCallbacks.getBuilder().drag(view, event);
                }
                // 更新上次触摸点的数据
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!config.isDrag) {
                    return;
                }
                switch (config.sidePattern) {
                    case RESULT_LEFT:
                    case RESULT_RIGHT:
                    case RESULT_BOTTOM:
                    case RESULT_HORIZONTAL:
                    case RESULT_VERTICAL:
                    case RESULT_SIDE: {
                        sideAnim(view, params, windowManager);
                        break;
                    }
                    default: {
                        if (config.callbacks != null) {
                            config.callbacks.dragEnd(view);
                        }
                        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
                            config.floatCallbacks.getBuilder().dragEnd(view);
                        }
                        break;
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void sideAnim(final View view, final WindowManager.LayoutParams params, final WindowManager windowManager) {
        initDistanceValue(params, view);
        boolean isX = false;
        int end = 0;
        switch (config.sidePattern) {
            case RESULT_LEFT: {
                isX = true;
                end = 0;
                break;
            }
            case RESULT_RIGHT: {
                isX = true;
                end = params.x + rightDistance;
                break;
            }
            case RESULT_HORIZONTAL: {
                isX = true;
                end = 0;
                if (leftDistance < rightDistance) {
                    end = 0;
                } else {
                    end = params.x + rightDistance;
                }
                break;
            }
            case RESULT_TOP: {
                isX = false;
                end = 0;
                break;
            }
            case RESULT_BOTTOM: {
                isX = false;
                // 不要轻易使用此相关模式，需要考虑虚拟导航栏的情况
                if (hasStatusBar) {
                    end = emptyHeight - statusBarHeight(view);
                } else {
                    end = emptyHeight;
                }
                break;
            }
            case RESULT_VERTICAL: {
                isX = false;
                if (topDistance < bottomDistance) {
                    end = 0;
                } else {
                    if (hasStatusBar) {
                        end = emptyHeight - statusBarHeight(view);
                    } else {
                        end = emptyHeight;
                    }
                }
                break;
            }
            case RESULT_SIDE: {
                if (minX < minY) {
                    isX = true;
                    if (leftDistance < rightDistance) {
                        end = 0;
                    } else {
                        end = params.x + rightDistance;
                    }
                } else {
                    isX = false;
                    if (topDistance < bottomDistance) {
                        end = 0;
                    } else {
                        if (hasStatusBar) {
                            end = emptyHeight - statusBarHeight(view);
                        } else {
                            end = emptyHeight;
                        }
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
        final boolean isY = !isX;
        final ValueAnimator animator = ValueAnimator.ofInt((isX ? params.x : params.y), end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    if (!isY) {
                        params.x = (int) animation.getAnimatedValue();
                    } else {
                        params.y = (int) animation.getAnimatedValue();
                    }
                    // 极端情况，还没吸附就调用了关闭浮窗，会导致吸附闪退
                    windowManager.updateViewLayout(view, params);
                } catch (Exception e) {
                    animator.cancel();
                }
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                config.isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dragEnd(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                dragEnd(view);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void dragEnd(View view) {
        config.isAnim = false;
        if (config.callbacks != null) {
            config.callbacks.dragEnd(view);
        }
        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
            config.floatCallbacks.getBuilder().dragEnd(view);
        }
    }

    /**
     * 计算一些边界距离数据
     */
    private void initDistanceValue(WindowManager.LayoutParams params, View view) {
        leftDistance = params.x;
        rightDistance = parentWidth - (leftDistance + view.getRight());
        topDistance = params.y;
        if (hasStatusBar) {
            bottomDistance = parentHeight - statusBarHeight(view) - topDistance - view.getHeight();
        } else {
            bottomDistance = parentHeight - topDistance - view.getHeight();
        }

        minX = Math.min(leftDistance, rightDistance);
        minY = Math.min(topDistance, bottomDistance);
    }


    private int statusBarHeight(View view) {
        return DisplayUtils.statusBarHeight(view);
    }

}
