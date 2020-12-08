package com.yt.easyfloat.anim;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import com.yt.easyfloat.enums.SidePattern;
import com.yt.easyfloat.interfaces.OnFloatAnimator;


public class AnimatorManager {
    private final OnFloatAnimator onFloatAnimator;
    private final View view;
    private final ViewGroup parentView;
    private final SidePattern sidePattern;

    public Animator enterAnim() {
        return onFloatAnimator != null ? onFloatAnimator.enterAnim(this.view, this.parentView, this.sidePattern) : null;
    }

    public Animator exitAnim() {
        return onFloatAnimator != null ? onFloatAnimator.exitAnim(this.view, this.parentView, this.sidePattern) : null;
    }

    public AnimatorManager(OnFloatAnimator onFloatAnimator, View view, ViewGroup parentView, SidePattern sidePattern) {
        this.onFloatAnimator = onFloatAnimator;
        this.view = view;
        this.parentView = parentView;
        this.sidePattern = sidePattern;
    }
}
