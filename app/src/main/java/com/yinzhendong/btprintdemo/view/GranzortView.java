package com.yinzhendong.btprintdemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yinzhendong.btprintdemo.R;

/**
 * Created by YinZhendong on 2017/8/28.
 */

public class GranzortView extends View {

    private static final String TAG = "GranzortView";
    private Paint paint;

    private Path innerCircle;//内部圆
    private Path outerCircle;//外部圆
    private Path trangle1;//第一个三角形
    private Path trangle2;//第二个三角形
    private Path drawPath;//用于截取路径的Path

    private PathMeasure pathMeasure;
    private float mViewWidth;
    private float mViewHeight;

    private long duration = 3000;
    private ValueAnimator valueAnimator;

    private Handler mHandler;
    private float distance;//当前动画执行的百分比取值为0-1
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private Animator.AnimatorListener animatorListener;

    private State mCurrentState = State.CIRCLE_STATE;
    //三个阶段的枚举
    private enum State {
        CIRCLE_STATE,
        TRANGLE_STATE,
        FINISH_STATE
    }

    public GranzortView(Context context) {
        super(context);
    }
    public GranzortView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GranzortView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.colorPrimary));
        canvas.save();

        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        switch (mCurrentState) {
            case CIRCLE_STATE:
                drawPath.reset();
                pathMeasure.setPath(innerCircle, false);
                pathMeasure.getSegment(0, distance * pathMeasure.getLength(), drawPath, true);
                canvas.drawPath(drawPath, paint);
                pathMeasure.setPath(outerCircle, false);
                drawPath.reset();
                pathMeasure.getSegment(0, distance * pathMeasure.getLength(), drawPath, true);
                canvas.drawPath(drawPath, paint);
                break;
            case TRANGLE_STATE:
                canvas.drawPath(innerCircle, paint);
                canvas.drawPath(outerCircle, paint);
                drawPath.reset();
                pathMeasure.setPath(trangle1, false);
                float stopD = distance * pathMeasure.getLength();
                float startD = stopD - (0.5f - Math.abs(0.5f - distance)) * 200;
                pathMeasure.getSegment(startD, stopD, drawPath, true);
                canvas.drawPath(drawPath, paint);
                drawPath.reset();
                pathMeasure.setPath(trangle2, false);
                pathMeasure.getSegment(startD, stopD, drawPath, true);
                canvas.drawPath(drawPath, paint);
                break;
            case FINISH_STATE:
                canvas.drawPath(innerCircle, paint);
                canvas.drawPath(outerCircle, paint);
                drawPath.reset();
                pathMeasure.setPath(trangle1, false);
                pathMeasure.getSegment(0, distance * pathMeasure.getLength(), drawPath, true);
                canvas.drawPath(drawPath, paint);
                drawPath.reset();
                pathMeasure.setPath(trangle2, false);
                pathMeasure.getSegment(0, distance * pathMeasure.getLength(), drawPath, true);
                canvas.drawPath(drawPath, paint);
                break;
        }
        canvas.restore();
    }

    private void init() {
        initPaint();
        initPath();
        initHandler();
        initAnimatorListener();
        initAnimator();
    }

    private void initAnimator() {

    }

    private void initAnimatorListener() {
        animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                distance = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i(TAG, "start:" + mCurrentState + "_");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "end:" + mCurrentState + "_");
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void initHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (mCurrentState) {
                    case CIRCLE_STATE:
                        mCurrentState = State.TRANGLE_STATE;
                        valueAnimator.start();
                        break;
                    case TRANGLE_STATE:
                        mCurrentState = State.FINISH_STATE;
                        valueAnimator.start();
                        break;
                }
            }
        };
    }

    private void initPath() {
        innerCircle = new Path();
        outerCircle = new Path();
        trangle1 = new Path();
        trangle2 = new Path();
        drawPath = new Path();

        pathMeasure = new PathMeasure();
        RectF innerRect = new RectF(-220, -220, 220, 220);
        RectF outerRect = new RectF(-280, -280, 280, 280);
        innerCircle.addArc(innerRect, 150, -259.9f); // 不能取360f，否则可能造成测量到的值不准确
        outerCircle.addArc(outerRect, 50, -359.9f);

        pathMeasure.setPath(innerCircle, false);

        float[] pos = new float[2];
        pathMeasure.getPosTan(0, pos, null);
        trangle1.moveTo(pos[0], pos[1]);
        pathMeasure.getPosTan((1f / 3f) * pathMeasure.getLength(), pos, null);
        Log.i(TAG, "pos : " + pos[0] + "  " + pos[1]);
        trangle1.lineTo(pos[0], pos[1]);

        pathMeasure.getPosTan((2f / 3f) * pathMeasure.getLength(), pos, null);
        trangle1.lineTo(pos[0], pos[1]);
        trangle1.close();

        pathMeasure.getPosTan((2f / 3f) * pathMeasure.getLength(), pos, null);
        Matrix matrix = new Matrix();
        matrix.postRotate(-180);
        trangle1.transform(matrix, trangle2);

    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setShadowLayer(15, 0, 0, Color.WHITE);//白色光影效果

    }
}