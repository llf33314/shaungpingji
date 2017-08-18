package com.gt.doubledisplay.web;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
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
        /*int r=255-255*currentProgress/100;
        int g=255*currentProgress/100;
        mPaint.setColor(Color.rgb(r,g,0));*/

        Shader mShader = new LinearGradient(0,0,this.getWidth(),this.getHeight(),new int[] {Color.RED,Color.GREEN},null,Shader.TileMode.REPEAT);

        mPaint.setShader(mShader);

        //当前进度占的像素
       int current= (int)(((double)currentProgress)/((double)max)*getWidth());
        canvas.drawRect(0,0,current,getHeight(),mPaint);
    }
    public void setProgress(int progress){
        this.currentProgress=progress;
        invalidate();
    }
}
