package com.yt.easyfloat.example;

import android.app.Application;
import android.content.Context;

import com.yt.easyfloat.BuildConfig;
import com.yt.easyfloat.EasyFloat;

public class App extends Application {
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        EasyFloat.init(this, BuildConfig.DEBUG);
    }
}
