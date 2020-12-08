package com.yt.easyfloat.example.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.yt.easyfloat.example.R;

public class RoundProgressBar extends View {

    int mMaxStep;//最大数值
    int progress;//当前进度值
    int mRoundProgressColor;//半弧进度值
    int mTextColor;//字体色值
    int mRoundColor;//半弧底色
    int mRadius;//半径
    int centerX;//x轴
    int centerY;//y轴
    int mPercent;//百分比
    float mRoundWidth;//弧形宽度
    Paint mPaint;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.WHITE);
        mRoundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.parseColor("#fd9426"));
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColors, Color.GREEN);
        mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        mMaxStep = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        mTypedArray.recycle();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**画背景圆弧*/
        //int centerX = mViewWidth / 2;
        //int centerY = mViewHeight / 2;
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        // 设置圆弧画笔的宽度
        mPaint.setStrokeWidth(mRoundWidth);
        // 设置为 ROUND
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 设置画笔颜色
        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        // 半径
        mRadius = (int) (centerX - mRoundWidth);
        RectF oval = new RectF(centerX - mRadius, centerY - mRadius, centerX + mRadius, centerY + mRadius);
        // 画背景圆弧
        canvas.drawArc(oval, -180, 180, true, mPaint);

        /** 画进度圆弧*/
        mPaint.setColor(mRoundProgressColor);
        // 计算当前百分比
        //float percent = (float) progress / mMaxStep;
        // 根据当前百分比计算圆弧扫描的角度
        canvas.drawArc(oval, -180, 180 * progress / mMaxStep, true, mPaint);

        /**画数字进度值* */
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(20);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        mPercent = (int) (((float) progress / (float) mMaxStep) * 100);
        //画进度值显示位置   这里也可将"progress"直接当进度值
        canvas.drawText(mPercent + "", getWidth() / 2, getHeight() / 2, mPaint);
    }

    // 设置当前最大步数
    public synchronized void setMaxStep(int maxStep) {
        if (maxStep < 0) {
            throw new IllegalArgumentException("maxStep 不能小于0!");
        }
        this.mMaxStep = maxStep;
    }

    // 设置进度
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress 不能小于0!");
        }
        if (progress > mMaxStep) {
            progress = mMaxStep;
        }
        if (progress <= mMaxStep) {
            this.progress = progress;
            // 重新刷新绘制 -> onDraw()
            postInvalidate();
        }
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized int getMaxStep() {
        return mMaxStep;
    }
}
