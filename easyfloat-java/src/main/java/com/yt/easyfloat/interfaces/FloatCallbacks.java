package com.yt.easyfloat.interfaces;

import android.view.MotionEvent;
import android.view.View;

public class FloatCallbacks {
    private Builder builder;

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public void registerListener(Builder builder) {
        this.builder = builder;
    }

    public static abstract class Builder {
        public void createdResult(boolean isCreated, String msg, View view) {

        }

        public void show(View view) {

        }

        public void hide(View view) {

        }

        public void dismiss() {

        }

        public void touchEvent(View view, MotionEvent event) {
        }


        public void drag(View view, MotionEvent event) {
        }

        public void dragEnd(View view) {
        }

    }
}
