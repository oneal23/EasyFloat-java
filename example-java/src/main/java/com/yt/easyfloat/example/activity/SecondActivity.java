package com.yt.easyfloat.example.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yt.easyfloat.EasyFloat;
import com.yt.easyfloat.anim.DefaultAnimator;
import com.yt.easyfloat.data.FloatConfig;
import com.yt.easyfloat.enums.SidePattern;
import com.yt.easyfloat.example.R;
import com.yt.easyfloat.interfaces.OnFloatAnimator;
import com.yt.easyfloat.interfaces.OnInvokeView;
import com.yt.easyfloat.widget.activityfloat.FloatingView;

import java.util.Random;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    protected TextView tvShow;
    protected TextView openThird;
    protected FloatingView floatingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_second);
        initView();
    }

    private void initView() {
        findViewById(R.id.tvShow).setOnClickListener(this);
        findViewById(R.id.openThird).setOnClickListener(this);
        floatingView = (FloatingView) findViewById(R.id.floatingView);
        FloatConfig config = new FloatConfig();
        config.floatAnimator = new DefaultAnimator() {
            @Override
            public Animator enterAnim(View view, ViewGroup parentView, SidePattern sidePattern) {
                AnimatorSet animator = new AnimatorSet();
                animator.play(ObjectAnimator.ofFloat(view, "alpha", 0f, 0.3f, 1f))
                        .with(ObjectAnimator.ofFloat(view, "scaleX", 0f, 2f, 1f))
                        .with(ObjectAnimator.ofFloat(view, "scaleY", 0f, 2f, 1f))
                        .before(ObjectAnimator.ofFloat(view, "rotation", 0f, 360f, 0f));
                animator.setDuration(1000L);

                return animator;
            }
        };
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvShow) {

            EasyFloat.with(this)
                    .setLayout(R.layout.float_top_dialog, new OnInvokeView() {

                        @Override
                        public void invoke(final View view) {
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    EasyFloat.dismiss(SecondActivity.this, view.getTag() + "");
                                }
                            }, 2500L);
                        }
                    })
                    .setMatchParent(true, false)
                    .setSidePattern(SidePattern.TOP)
                    .setDragEnable(false)
                    .setTag(new Random().nextDouble() + "")
                    .setAnimator(new DefaultAnimator() {
                        @Override
                        public Animator enterAnim(View view, ViewGroup parentView, SidePattern sidePattern) {
                            Animator animator = super.enterAnim(view, parentView, sidePattern);
                            animator.setInterpolator(new BounceInterpolator());
                            return animator;
                        }

                        @Override
                        public Animator exitAnim(View view, ViewGroup parentView, SidePattern sidePattern) {
                            Animator animator =  super.exitAnim(view, parentView, sidePattern);
                            animator.setDuration(300L);
                            return animator;
                        }
                    })
                    .show();
        } else if (view.getId() == R.id.openThird) {
            startActivity(new Intent(this, ThirdActivity.class));
        }
    }

}
