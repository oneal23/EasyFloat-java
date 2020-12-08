package com.yt.easyfloat.anim;

public class Triple {
    private String animType;
    private float startValue;
    private float endValue;

    public Triple(String animType, float startValue, float endValue) {
        this.animType = animType;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    public String getAnimType() {
        return animType;
    }

    public void setAnimType(String animType) {
        this.animType = animType;
    }

    public float getStartValue() {
        return startValue;
    }

    public void setStartValue(float startValue) {
        this.startValue = startValue;
    }

    public float getEndValue() {
        return endValue;
    }

    public void setEndValue(float endValue) {
        this.endValue = endValue;
    }
}
