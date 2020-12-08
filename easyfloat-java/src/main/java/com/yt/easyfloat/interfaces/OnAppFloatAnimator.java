package com.yt.easyfloat.interfaces;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.WindowManager;

import com.yt.easyfloat.enums.SidePattern;

public interface OnAppFloatAnimator {
    ValueAnimator enterAnim(View view, WindowManager.LayoutParams params, WindowManager windowManager, SidePattern pattern);
    ValueAnimator exitAnim(View view, WindowManager.LayoutParams params, WindowManager windowManager, SidePattern pattern);
}
