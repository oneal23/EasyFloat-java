package com.yt.easyfloat.interfaces;

import android.view.MotionEvent;
import android.view.View;


public interface OnFloatCallbacks {
    void createdResult(boolean isCreated, String msg, View view);

    void show(View view);

    void hide(View view);

    void dismiss();

    void touchEvent(View view, MotionEvent event);

    void drag(View view, MotionEvent event);

    void dragEnd(View view);
}
