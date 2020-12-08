package com.yt.easyfloat.example.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ScaleImage extends AppCompatImageView {

    private float touchDownX = 0f;
    private float touchDownY = 0f;
    private onScaledListener onScaledListener;

    public static interface onScaledListener {
        void onScaled(float x, float y, MotionEvent event);
    }

    public void setOnScaledListener(ScaleImage.onScaledListener onScaledListener) {
        this.onScaledListener = onScaledListener;
    }

    public ScaleImage(@NonNull Context context) {
        super(context);
    }

    public ScaleImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return super.onTouchEvent(event);
        }

        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownX = event.getX();
                touchDownY = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (onScaledListener != null) {
                    onScaledListener.onScaled(event.getX() - touchDownX, event.getY() - touchDownY, event);
                }
                break;
            }
            default: {
                break;
            }

        }
        return true;
    }
}
