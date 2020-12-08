package com.yt.easyfloat.widget.activityfloat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yt.easyfloat.data.FloatConfig;

public class FloatingView extends AbstractDragFloatingView{
    public FloatingView(Context context) {
        super(context);
    }

    public FloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setConfig(FloatConfig config) {
        this.config = config;
        initView(getContext());
        requestLayout();
    }

    @Override
    int getLayoutId() {
        if (config != null) {
            return config.layoutId;
        }
        return 0;
    }

    @Override
    void renderView(View view) {

    }
}
