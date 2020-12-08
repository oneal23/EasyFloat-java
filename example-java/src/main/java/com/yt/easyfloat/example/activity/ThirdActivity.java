package com.yt.easyfloat.example.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yt.easyfloat.EasyFloat;
import com.yt.easyfloat.enums.ShowPattern;
import com.yt.easyfloat.example.R;
import com.yt.easyfloat.interfaces.OnInvokeView;
import com.yt.easyfloat.utils.InputMethodUtils;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_third);
        initView();
    }

    private void initView() {
        findViewById(R.id.openEditTextFloat).setOnClickListener(this);
        findViewById(R.id.changeBackground).setOnClickListener(this);
        findViewById(R.id.recoverBackground).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.openEditTextFloat) {
            showEditTextFloat("editTextFloat");
        } else if (view.getId() == R.id.changeBackground) {
            View floatView = EasyFloat.getAppFloatView();
            if (floatView != null) {
                View view1 = floatView.findViewById(R.id.rlContent);
                if (view1 != null) {
                    view1.setBackgroundColor(ThirdActivity.this.getResources().getColor(R.color.violet));
                }
            }
        } else if (view.getId() == R.id.recoverBackground) {
            View floatView = EasyFloat.getAppFloatView();
            if (floatView != null) {
                View view1 = floatView.findViewById(R.id.rlContent);
                if (view1 != null) {
                    view1.setBackgroundColor(ThirdActivity.this.getResources().getColor(R.color.translucent));
                }
            }
        }
    }

    private void showEditTextFloat(final String tag) {
        EasyFloat.with(this)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setGravity(Gravity.CENTER, 0, -300)
                .setTag(tag)
                .hasEditText(true)
                .setLayout(R.layout.float_edit, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {
                        final EditText editText = (EditText)view.findViewById(R.id.editText);
                        editText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                InputMethodUtils.openInputMethod(editText, tag);
                            }
                        });
                        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    InputMethodUtils.openInputMethod(editText, tag);
                                }
                            }
                        });
                        view.findViewById(R.id.tvCloseFloat).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EasyFloat.dismissAppFloat(tag);
                            }
                        });
                    }
                }).show();
    }

//    private fun showFloat() {
//
//        EasyFloat.with(this).setLayout(R.layout.float_app).show()
//
//        EasyFloat.with(this)
//                // 设置浮窗xml布局文件，并可设置详细信息
//                .setLayout(R.layout.float_app, OnInvokeView { })
//        // 设置我们传入xml布局的详细信息
//            .invokeView(OnInvokeView { })
//        // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示
//            .setShowPattern(ShowPattern.ALL_TIME)
//                // 设置吸附方式，共15种模式，详情参考SidePattern
//                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
//                // 设置浮窗的标签，用于区分多个浮窗
//                .setTag("testFloat")
//                // 设置浮窗是否可拖拽
//                .setDragEnable(true)
//                // 系统浮窗是否包含EditText，仅针对系统浮窗，默认不包含
//                .hasEditText(false)
//                // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
//                .setLocation(100, 200)
//                // 设置浮窗的对齐方式和坐标偏移量
//                .setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 200)
//                // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
//                .setMatchParent(widthMatch = false, heightMatch = false)
//                // 设置Activity浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
//                .setAnimator(DefaultAnimator())
//                // 设置系统浮窗的出入动画，使用同上
//                .setAppFloatAnimator(AppFloatDefaultAnimator())
//                // 设置系统浮窗的不需要显示的页面
//                .setFilter(MainActivity::class.java, SecondActivity::class.java)
//        // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
//            .setDisplayHeight(OnDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) })
//        // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
//        // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
//            .registerCallback {
//            createResult { isCreated, msg, view -> }
//            show { }
//            hide { }
//            dismiss { }
//            touchEvent { view, motionEvent -> }
//            drag { view, motionEvent -> }
//            dragEnd { }
//        }
//            .registerCallbacks(object : OnFloatCallbacks {
//            override fun createdResult(isCreated: Boolean, msg: String?, view: View?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun show(view: View) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun hide(view: View) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun dismiss() {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun touchEvent(view: View, event: MotionEvent) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun drag(view: View, event: MotionEvent) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun dragEnd(view: View) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//        })
//        // 创建浮窗（这是关键哦😂）
//            .show()
//    }

}
