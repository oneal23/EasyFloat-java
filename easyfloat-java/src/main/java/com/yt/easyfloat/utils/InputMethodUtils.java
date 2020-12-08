package com.yt.easyfloat.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yt.easyfloat.widget.appfloat.AppFloatManager;
import com.yt.easyfloat.widget.appfloat.FloatManager;


public class InputMethodUtils {

    /**
     * 让系统浮窗获取焦点，并打开软键盘
     */
    public static void openInputMethod(final EditText editText, String tag) {
        AppFloatManager manager = FloatManager.getAppFloatManager(tag);
        if (manager != null) {
            manager.params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            manager.windowManager.updateViewLayout(manager.frameLayout, manager.params);
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
               InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
               if (inputMethodManager != null) {
                   inputMethodManager.showSoftInput(editText, 0);
               }
            }
        }, 100L);
    }

    /**
     * 当软键盘关闭时，调用此方法，移除系统浮窗的焦点，不然系统返回键无效
     */
    public static void closedInputMethod(String tag) {
        AppFloatManager manager = FloatManager.getAppFloatManager(tag);
        if (manager != null) {
            manager.params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            manager.windowManager.updateViewLayout(manager.frameLayout, manager.params);
        }
    }
}
