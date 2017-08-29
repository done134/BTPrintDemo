package com.yinzhendong.btprintdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yinzhendong.btprintdemo.R;

/**
 * Created by YinZhendong on 2017/8/29.
 */

public class SimpleView extends View {
    private static final String TAG = "SimpleView";
    Paint paint;


    public SimpleView(Context context) {
        this(context, null);
    }

    public SimpleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleView(Context context, AttributeSet attrs, @Nullable int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.colorPrimary));
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(200, 200, 150, paint);
    }
}