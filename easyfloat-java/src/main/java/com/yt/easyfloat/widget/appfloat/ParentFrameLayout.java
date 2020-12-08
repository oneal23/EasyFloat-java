package com.yt.easyfloat.widget.appfloat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.interfaces.OnFloatTouchListener;
import com.yt.easyfloat.utils.InputMethodUtils;

public class ParentFrameLayout extends FrameLayout {
    private FloatConfig config;
    OnFloatTouchListener touchListener = null;
    OnLayoutListener layoutListener = null;
    private boolean isCreated = false;

    // 布局绘制完成的接口，用于通知外部做一些View操作，不然无法获取view宽高
    public static interface OnLayoutListener {
        void onLayout();
    }

    public void setTouchListener(OnFloatTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public void setLayoutListener(OnLayoutListener layoutListener) {
        this.layoutListener = layoutListener;
    }

    public void setConfig(FloatConfig config) {
        this.config = config;
    }

    public ParentFrameLayout(Context context) {
        super(context);
    }

    public ParentFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ParentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初次绘制完成的时候，需要设置对齐方式、坐标偏移量、入场动画
        if (!isCreated) {
            isCreated = true;
            if (layoutListener != null) {
                layoutListener.onLayout();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null && touchListener != null){
            touchListener.onTouch(event);
        }
        return config.isDrag || super.onTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event != null && touchListener != null){
            touchListener.onTouch(event);
        }
        // 是拖拽事件就进行拦截，反之不拦截
        // ps：拦截后将不再回调该方法，会交给该view的onTouchEvent进行处理，所以后续事件需要在onTouchEvent中回调
        return config.isDrag || super.onInterceptTouchEvent(event);
    }

    /**
     * 按键转发到视图的分发方法，在这里关闭输入法
     */
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (config.hasEditText && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            InputMethodUtils.closedInputMethod(FloatManager.getTag(config.floatTag));
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (config.callbacks != null) {
            config.callbacks.dismiss();
        }
        if (config.floatCallbacks != null && config.floatCallbacks.getBuilder() != null) {
            config.floatCallbacks.getBuilder().dismiss();
        }
    }

}
