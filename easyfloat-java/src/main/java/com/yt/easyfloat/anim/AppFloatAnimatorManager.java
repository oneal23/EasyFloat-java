package com.yt.easyfloat.anim;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.WindowManager;

import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.interfaces.OnAppFloatAnimator;

public final class AppFloatAnimatorManager {
    private final View view;
    private final WindowManager.LayoutParams params;
    private final WindowManager windowManager;
    private final FloatConfig config;

    public final ValueAnimator enterAnim() {
        OnAppFloatAnimator animator = this.config.appFloatAnimator;
        return animator != null ? animator.enterAnim(this.view, this.params, this.windowManager, this.config.sidePattern) : null;
    }

    public final ValueAnimator exitAnim() {
        OnAppFloatAnimator animator = this.config.appFloatAnimator;
        return animator != null ? animator.exitAnim(this.view, this.params, this.windowManager, this.config.sidePattern) : null;
    }

    public AppFloatAnimatorManager(View view, WindowManager.LayoutParams params, WindowManager windowManager, FloatConfig config) {
        this.view = view;
        this.params = params;
        this.windowManager = windowManager;
        this.config = config;
    }
}
