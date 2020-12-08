package com.yt.easyfloat.interfaces;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import com.yt.easyfloat.enums.SidePattern;

public interface OnFloatAnimator {

    Animator enterAnim(View view, ViewGroup viewGroup, SidePattern pattern);


    Animator exitAnim(View view, ViewGroup viewGroup, SidePattern pattern);
}
