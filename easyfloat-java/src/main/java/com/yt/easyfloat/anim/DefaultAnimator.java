package com.yt.easyfloat.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.yt.easyfloat.enums.SidePattern;
import com.yt.easyfloat.interfaces.OnFloatAnimator;


public class DefaultAnimator implements OnFloatAnimator {
    private int leftDistance;
    private int rightDistance;
    private int topDistance;
    private int bottomDistance;
    private int minX;
    private int minY;
    private Rect floatRect = new Rect();
    private Rect parentRect = new Rect();

    @Override
    public Animator enterAnim(View view, ViewGroup parentView, SidePattern sidePattern) {
        this.initValue(view, parentView);
        Triple triple = animTriple(view, sidePattern);
        String animType = triple.getAnimType();
        float startValue = triple.getStartValue();
        float endValue = triple.getEndValue();
        return ObjectAnimator.ofFloat(view, animType, new float[]{startValue, endValue}).setDuration(500L);
    }

    @Override
    public Animator exitAnim(View view, ViewGroup parentView, SidePattern sidePattern) {
        this.initValue(view, parentView);
        Triple triple = animTriple(view, sidePattern);
        String animType = triple.getAnimType();
        float startValue = triple.getStartValue();
        float endValue = triple.getEndValue();
        return ObjectAnimator.ofFloat(view, animType, new float[]{endValue, startValue}).setDuration(500L);
    }

    private final Triple animTriple(View view, SidePattern sidePattern) {
        String animType = null;
        float startValue = 0;
        switch (sidePattern) {
            case LEFT: {
                animType = "translationX";
                startValue = leftValue(view);
                break;
            }
            case RIGHT: {
                animType = "translationX";
                startValue = rightValue(view);
                break;
            }
            case TOP: {
                animType = "translationY";
                startValue = topValue(view);
                break;
            }
            case BOTTOM: {
                animType = "translationY";
                startValue = bottomValue(view);
                //rightValue(view);
                break;
            }
            case DEFAULT:
            case AUTO_HORIZONTAL:
            case RESULT_HORIZONTAL: {
                animType = "translationX";
                if (leftDistance < rightDistance) {
                    startValue = leftValue(view);
                } else {
                    startValue = rightValue(view);
                }
                break;
            }
            case AUTO_VERTICAL:
            case RESULT_VERTICAL: {
                animType = "translationY";
                if (topDistance < bottomDistance) {
                    startValue = topValue(view);
                } else {
                    startValue = bottomValue(view);
                }
                break;
            }
            default: {
                if (minX <= minY) {
                    animType = "translationX";
                    if (leftDistance < rightDistance) {
                        startValue = leftValue(view);
                    } else {
                        startValue = rightValue(view);
                    }
                } else {
                    animType = "translationY";
                    if (topDistance < bottomDistance) {
                        startValue = topValue(view);
                    } else {
                        startValue = bottomValue(view);
                    }
                }
            }
            break;
        }
        float endValue = 0;
        if (animType == "translationX") {
            endValue = view.getTranslationX();
        } else {
            endValue = view.getTranslationY();
        }
        return new Triple(animType, startValue, endValue);
    }

    private float leftValue(View view) {
        return (float) (-(this.leftDistance + view.getWidth())) + view.getTranslationX();
    }

    private float rightValue(View view) {
        return (float) (this.rightDistance + view.getWidth()) + view.getTranslationX();
    }

    private float topValue(View view) {
        return (float) (-(this.topDistance + view.getHeight())) + view.getTranslationY();
    }

    private float bottomValue(View view) {
        return (float) (this.bottomDistance + view.getHeight()) + view.getTranslationY();
    }

    private void initValue(View view, ViewGroup parentView) {

        view.getGlobalVisibleRect(floatRect);
        parentView.getGlobalVisibleRect(parentRect);

        leftDistance = floatRect.left;
        rightDistance = parentRect.right - floatRect.right;
        topDistance = floatRect.top - parentRect.top;
        bottomDistance = parentRect.bottom - floatRect.bottom;

        minX = Math.min(leftDistance, rightDistance);
        minY = Math.min(topDistance, bottomDistance);
    }
}

