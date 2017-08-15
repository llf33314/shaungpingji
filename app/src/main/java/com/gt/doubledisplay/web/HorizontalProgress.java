package com.gt.doubledisplay.web;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gt.doubledisplay.R;

/**
 * Created by wzb on 2017/8/2 0002.
 */

public class HorizontalProgress extends View {
    private int max=100;
    private int currentProgress=0;
    private int progressColor;
    private Paint mPaint;
    public HorizontalProgress(Context context) {
        super(context);
        progressColor=context.getResources().getColor(R.color.webviewProgress);
        initPaint();

    }

    public HorizontalProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgress);
        max=a.getInteger(R.styleable.HorizontalProgress_max,100);
        currentProgress=a.getInteger(R.styleable.HorizontalProgress_currentProgress,0);
        progressColor=a.getColor(R.styleable.HorizontalProgress_progressColor,context.getResources().getColor(R.color.gray));
        initPaint();
        a.recycle();
    }

    private void initPaint(){
        mPaint=new Paint();
        mPaint.setColor(progressColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //当前进度占的像素
       int current= (int)(((double)currentProgress)/((double)max)*getWidth());
        canvas.drawRect(0,0,current,getHeight(),mPaint);
    }
    public void setProgress(int progress){
        this.currentProgress=progress;
        invalidate();
    }
}
