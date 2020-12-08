package com.yt.easyfloat.data;

import android.util.Pair;
import android.view.View;

import com.yt.easyfloat.anim.AppFloatDefaultAnimator;
import com.yt.easyfloat.anim.DefaultAnimator;
import com.yt.easyfloat.enums.ShowPattern;
import com.yt.easyfloat.enums.SidePattern;
import com.yt.easyfloat.interfaces.FloatCallbacks;
import com.yt.easyfloat.interfaces.OnAppFloatAnimator;
import com.yt.easyfloat.interfaces.OnDisplayHeight;
import com.yt.easyfloat.interfaces.OnFloatAnimator;
import com.yt.easyfloat.interfaces.OnFloatCallbacks;
import com.yt.easyfloat.interfaces.OnInvokeView;
import com.yt.easyfloat.utils.DefaultDisplayHeight;

import java.util.HashSet;
import java.util.Set;

public class FloatConfig {
    // 浮窗的xml布局文件
    public int layoutId;
    public View layoutView;

    // 当前浮窗的tag
    public String floatTag = null;

    // 是否可拖拽
    public boolean dragEnable = true;
    // 是否正在被拖拽
    public boolean isDrag = false;
    // 是否正在执行动画
    public boolean isAnim = false;
    // 是否显示
    public boolean isShow = false;
    // 是否包含EditText
    public boolean hasEditText = false;

    // 浮窗的吸附方式（默认不吸附，拖到哪里是哪里）
    public SidePattern sidePattern = SidePattern.DEFAULT;

    // 浮窗显示类型（默认只在当前页显示）
    public ShowPattern showPattern = ShowPattern.CURRENT_ACTIVITY;

    // 宽高是否充满父布局
    public boolean widthMatch = false;
    public boolean heightMatch = false;

    // 浮窗的摆放方式，使用系统的Gravity属性
    public int gravity = 0;
    // 坐标的偏移量
    public Pair<Integer, Integer> offsetPair = new Pair<Integer, Integer>(0, 0);
    // 固定的初始坐标，左上角坐标
    public Pair<Integer, Integer> locationPair = new Pair<Integer, Integer>(0, 0);
    // ps：优先使用固定坐标，若固定坐标不为原点坐标，gravity属性和offset属性无效

    // Callbacks
    public OnInvokeView invokeView = null;

    public OnFloatCallbacks callbacks = null;
    // 通过Kotlin DSL设置回调，无需复写全部方法，按需复写
    public FloatCallbacks floatCallbacks;

    // 出入动画
    public OnFloatAnimator floatAnimator = new DefaultAnimator();
    public OnAppFloatAnimator appFloatAnimator = new AppFloatDefaultAnimator();


    // 设置屏幕的有效显示高度（不包含虚拟导航栏的高度），仅针对系统浮窗，一般不用复写
    public OnDisplayHeight displayHeight = new DefaultDisplayHeight();

    // 不需要显示系统浮窗的页面集合，参数为类名
//    val filterSet: MutableSet<String> = mutableSetOf(),
    public Set<String> filterSet = new HashSet<String>();
    // 是否设置，当前创建的页面也被过滤
    public boolean filterSelf = false;
    // 是否需要显示，当过滤信息匹配上时，该值为false（用户手动调用隐藏，该值也为false，相当于手动过滤）
    public boolean needShow = true;

}
