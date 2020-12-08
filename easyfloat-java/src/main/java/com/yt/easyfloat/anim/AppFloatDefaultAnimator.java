package com.yt.easyfloat.anim;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;

import com.yt.easyfloat.enums.SidePattern;
import com.yt.easyfloat.interfaces.OnAppFloatAnimator;

public class AppFloatDefaultAnimator implements OnAppFloatAnimator {
    @Override
    public ValueAnimator enterAnim(final View view, final WindowManager.LayoutParams params, final WindowManager windowManager, SidePattern pattern) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initValue(view, params, windowManager), params.x).setDuration(500L);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.x = (int) animation.getAnimatedValue();
                windowManager.updateViewLayout(view, params);
            }
        });
        return valueAnimator;
    }

    @Override
    public ValueAnimator exitAnim(final View view, final WindowManager.LayoutParams params, final WindowManager windowManager, SidePattern pattern) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(params.x, initValue(view, params, windowManager)).setDuration(500L);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.x = (int) animation.getAnimatedValue();
                windowManager.updateViewLayout(view, params);
            }
        });
        return valueAnimator;
    }

    private int initValue(View view, WindowManager.LayoutParams params, WindowManager windowManager) {
        Rect parentRect = new Rect();
        windowManager.getDefaultDisplay().getRectSize(parentRect);
        int leftDistance = params.x;
        int rightDistance = (int) (parentRect.right - (leftDistance + view.getRight()));
        if (leftDistance < rightDistance) {
            return -view.getRight();
        } else {
            return parentRect.right;
        }
    }
}
