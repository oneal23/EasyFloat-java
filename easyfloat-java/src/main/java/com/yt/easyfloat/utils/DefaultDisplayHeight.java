package com.yt.easyfloat.utils;

import android.content.Context;

import com.yt.easyfloat.interfaces.OnDisplayHeight;

public class DefaultDisplayHeight implements OnDisplayHeight {
    public int getDisplayRealHeight(Context context) {
        return DisplayUtils.rejectedNavHeight(context);
    }
}
