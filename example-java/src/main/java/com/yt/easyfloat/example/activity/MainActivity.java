package com.yt.easyfloat.example.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yt.easyfloat.EasyFloat;
import com.yt.easyfloat.enums.ShowPattern;
import com.yt.easyfloat.enums.SidePattern;
import com.yt.easyfloat.example.R;
import com.yt.easyfloat.example.widget.RoundProgressBar;
import com.yt.easyfloat.example.widget.ScaleImage;
import com.yt.easyfloat.interfaces.OnFloatCallbacks;
import com.yt.easyfloat.interfaces.OnInvokeView;
import com.yt.easyfloat.permission.PermissionUtils;
import com.yt.easyfloat.utils.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected TextView open1;
    protected TextView hide1;
    protected TextView show1;
    protected TextView dismiss1;
    protected TextView open2;
    protected TextView hide2;
    protected TextView show2;
    protected TextView dismiss2;
    protected TextView open3;
    protected TextView hide3;
    protected TextView show3;
    protected TextView dismiss3;
    protected TextView open4;
    protected TextView hide4;
    protected TextView show4;
    protected TextView dismiss4;
    protected TextView openSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        open1 = (TextView) findViewById(R.id.open1);
        open1.setOnClickListener(MainActivity.this);
        hide1 = (TextView) findViewById(R.id.hide1);
        hide1.setOnClickListener(MainActivity.this);
        show1 = (TextView) findViewById(R.id.show1);
        show1.setOnClickListener(MainActivity.this);
        dismiss1 = (TextView) findViewById(R.id.dismiss1);
        dismiss1.setOnClickListener(MainActivity.this);
        open2 = (TextView) findViewById(R.id.open2);
        open2.setOnClickListener(MainActivity.this);
        hide2 = (TextView) findViewById(R.id.hide2);
        hide2.setOnClickListener(MainActivity.this);
        show2 = (TextView) findViewById(R.id.show2);
        show2.setOnClickListener(MainActivity.this);
        dismiss2 = (TextView) findViewById(R.id.dismiss2);
        dismiss2.setOnClickListener(MainActivity.this);
        open3 = (TextView) findViewById(R.id.open3);
        open3.setOnClickListener(MainActivity.this);
        hide3 = (TextView) findViewById(R.id.hide3);
        hide3.setOnClickListener(MainActivity.this);
        show3 = (TextView) findViewById(R.id.show3);
        show3.setOnClickListener(MainActivity.this);
        dismiss3 = (TextView) findViewById(R.id.dismiss3);
        dismiss3.setOnClickListener(MainActivity.this);
        open4 = (TextView) findViewById(R.id.open4);
        open4.setOnClickListener(MainActivity.this);
        hide4 = (TextView) findViewById(R.id.hide4);
        hide4.setOnClickListener(MainActivity.this);
        show4 = (TextView) findViewById(R.id.show4);
        show4.setOnClickListener(MainActivity.this);
        dismiss4 = (TextView) findViewById(R.id.dismiss4);
        dismiss4.setOnClickListener(MainActivity.this);
        openSecond = (TextView) findViewById(R.id.openSecond);
        openSecond.setOnClickListener(MainActivity.this);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.open1) {
            showActvityFloat();
        } else if (view.getId() == R.id.hide1) {
            EasyFloat.hide(this);
        } else if (view.getId() == R.id.show1) {
            EasyFloat.show(this);
        } else if (view.getId() == R.id.dismiss1) {
            EasyFloat.dismiss(this);
        } else if (view.getId() == R.id.open2) {
            showActvityFloat2();
        } else if (view.getId() == R.id.hide2) {
            EasyFloat.hide(this, "seekBar");
        } else if (view.getId() == R.id.show2) {
            EasyFloat.show(this, "seekBar");
        } else if (view.getId() == R.id.dismiss2) {
            EasyFloat.dismiss(this, "seekBar");
        } else if (view.getId() == R.id.open3) {
            checkPermission();
        } else if (view.getId() == R.id.hide3) {
            EasyFloat.hideAppFloat();
        } else if (view.getId() == R.id.show3) {
            EasyFloat.showAppFloat();
        } else if (view.getId() == R.id.dismiss3) {
            EasyFloat.dismissAppFloat();
        } else if (view.getId() == R.id.open4) {
            checkPermission("scaleFloat");
        } else if (view.getId() == R.id.hide4) {
            EasyFloat.hideAppFloat("scaleFloat");
        } else if (view.getId() == R.id.show4) {
            EasyFloat.showAppFloat("scaleFloat");
        } else if (view.getId() == R.id.dismiss4) {
            EasyFloat.dismissAppFloat("scaleFloat");
        } else if (view.getId() == R.id.openSecond) {
            startActivity(new Intent(this, SecondActivity.class));
        }
    }

    private void showActvityFloat() {
        EasyFloat.with(this)
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setGravity(Gravity.END, 0, 100)
                .setLayout(R.layout.float_custom, new OnInvokeView() {
                            @Override
                            public void invoke(View view) {
                                view.findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        toast("onClick");
                                    }
                                });
                            }
                        }
                ).registerCallbacks(new OnFloatCallbacks() {
            @Override
            public void createdResult(boolean isCreated, String msg, View view) {
                Logger.e("createdResult isCreated=" + isCreated + ",msg=" + msg + ",view=" + view);
            }

            @Override
            public void show(View View) {
                toast("show");
            }

            @Override
            public void hide(View View) {
                toast("hide");
            }

            @Override
            public void dismiss() {
                toast("dismiss");
            }

            @Override
            public void touchEvent(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView textView = (TextView) view.findViewById(R.id.textView);
                    textView.setText("拖一下试试");
                    textView.setBackgroundResource(R.drawable.corners_green);
                }
            }

            @Override
            public void drag(View view, MotionEvent event) {
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText("我被拖拽...");
                textView.setBackgroundResource(R.drawable.corners_green);

            }

            @Override
            public void dragEnd(View view) {
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText("我被拖拽...");
                int[] locations = new int[2];
                textView.getLocationOnScreen(locations);
                textView.setBackgroundResource(locations[0] > 0 ? R.drawable.corners_left : R.drawable.corners_right);
            }
        }).show();
    }

    private void showActvityFloat2() {
        // 改变浮窗1的文字
//        EasyFloat.getFloatView()?.findViewById<TextView>(R.id.textView)?.text = "恭喜浮窗2"

        EasyFloat.with(this)
                .setTag("seekBar")
                .setGravity(Gravity.CENTER, 0, 0)
                .setLayout(R.layout.float_seekbar, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {
                        view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EasyFloat.dismiss(MainActivity.this, "seekBar");
                            }
                        });

                        final TextView tvProgress = (TextView) view.findViewById(R.id.tvProgress);
                        tvProgress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toast(tvProgress.getText().toString());
                            }
                        });

                        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                tvProgress.setText("" + progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                    }
                }).show();
    }

    /**
     * 检测浮窗权限是否开启，若没有给与申请提示框（非必须，申请依旧是EasyFloat内部内保进行）
     */
    private void checkPermission(final String tag) {
        if (PermissionUtils.checkPermission(this)) {
            if (tag == null) {
                showAppFloat();
            } else {
                showAppFloat2(tag);
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
                    .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (tag == null) {
                                showAppFloat();
                            } else {
                                showAppFloat2(tag);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
    }

    private void checkPermission() {
        checkPermission(null);
    }

    private void showAppFloat() {
        EasyFloat.with(this)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setSidePattern(SidePattern.RESULT_SIDE)
                .setGravity(Gravity.CENTER, 0, 0)
                .setLayout(R.layout.float_app, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {
                        view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EasyFloat.dismissAppFloat();
                            }
                        });

                        view.findViewById(R.id.tvOpenMain).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                            }
                        });

                        ((CheckBox) view.findViewById(R.id.checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                EasyFloat.appFloatDragEnable(isChecked);
                            }
                        });

                        final RoundProgressBar roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
                        roundProgressBar.setProgress(66);
                        roundProgressBar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toast(roundProgressBar.getProgress() + "");
                            }
                        });

                        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                roundProgressBar.setProgress(progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                    }
                }).show();
    }

    private void showAppFloat2(final String tag) {
        EasyFloat.with(this)
                .setTag(tag)
                .setShowPattern(ShowPattern.FOREGROUND)
                .setLocation(100, 100)
                .setAppFloatAnimator(null)
                .setFilter(SecondActivity.class)
                .setLayout(R.layout.float_app_scale, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {

                        final RelativeLayout rlContent = (RelativeLayout) view.findViewById(R.id.rlContent);
                        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rlContent.getLayoutParams();
                        ScaleImage scaleImage = (ScaleImage) view.findViewById(R.id.ivScale);
                        scaleImage.setOnScaledListener(new ScaleImage.onScaledListener() {
                            @Override
                            public void onScaled(float x, float y, MotionEvent event) {
                                params.width = (int) Math.max(params.width + x, 100);
                                params.height = (int) Math.max(params.height + y, 100);
                                rlContent.setLayoutParams(params);
                            }
                        });
                        view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EasyFloat.dismissAppFloat(tag);
                            }
                        });
                    }
                }).show();
    }
}
