package com.laewoong.gooeyeffectview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by laewoong on 2018. 3. 17..
 * https://laewoong.github.io
 */

public class GooeyEffectAinmView extends View {

    private Path mLeftCirclePath;
    private Path mRightCirclePath;

    private Paint mPaint;

    private int mCenterX;
    private int mCenterY;

    private float mCurRadius;
    private float mGap;

    final float SEGMENT_NUM         = 20f;
    final float RADIUS              = 100f;
    final float ANIM_START_VALUE    = 0f;
    final float ANIM_END_VAULE      = 250f;
    final long  ANIM_DURATION       = 3000;

    private AnimatorSet mAinmSet;

    public GooeyEffectAinmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mLeftCirclePath = new Path();
        mRightCirclePath = new Path();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPaint.setColor(Color.BLUE);
        //mPaint.setStyle(Paint.Style.STROKE);
        //mPaint.setStrokeWidth(10f);

        mCurRadius = RADIUS;
        mGap = 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX = w/2;
        mCenterY = h/2;

        mPaint.setShader(new LinearGradient((int)(w*0.1), 0, (int)(w*0.9), 0,
                Color.parseColor("#3d6adb"), Color.parseColor("#ff6893"),
                Shader.TileMode.CLAMP));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAinmSet = new AnimatorSet();

        mAinmSet.playSequentially(
                ObjectAnimator.ofFloat(this, "gap", ANIM_START_VALUE, ANIM_END_VAULE),
                ObjectAnimator.ofFloat(this, "gap", ANIM_END_VAULE, ANIM_START_VALUE));
        //mAinmSet.setInterpolator(new FastOutLinearInInterpolator());
        mAinmSet.setInterpolator(new ElasticInterpolator());
        mAinmSet.setDuration(ANIM_DURATION);
        mAinmSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.start();
            }
        });
        mAinmSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAinmSet.end();
        mAinmSet = null;
    }

    public void setGap(float gap) {
        mGap = gap;
        mCurRadius = RADIUS - 15f*(gap/ ANIM_END_VAULE);
        invalidate();
    }

    public float getGap() {
        return mGap;
    }

    public void initGooeyPath() {

        mLeftCirclePath.reset();
        mRightCirclePath.reset();

        mLeftCirclePath.addCircle((mCenterX - mGap), mCenterY, mCurRadius, Path.Direction.CW);
        mRightCirclePath.addCircle((mCenterX + mGap), mCenterY, mCurRadius, Path.Direction.CW);
        mLeftCirclePath.op(mRightCirclePath, Path.Op.UNION);

        PathMeasure pm = new PathMeasure(mLeftCirclePath, true);
        DiscretePathEffect discretePathEffect= new DiscretePathEffect(pm.getLength()/SEGMENT_NUM, 0);

        CornerPathEffect cornerPathEffect = new CornerPathEffect(50);

        ComposePathEffect pathEffect = new ComposePathEffect(cornerPathEffect, discretePathEffect);

        mPaint.setPathEffect( pathEffect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initGooeyPath();
        canvas.drawPath(mLeftCirclePath, mPaint);
    }

    public static class ElasticInterpolator implements TimeInterpolator {

        @Override
        public float getInterpolation(float t) {
            float p = 0.4f;

            return (float)(Math.pow(2,-10*t) * Math.sin((t-p/4)*(2*Math.PI)/p) + 1);
        }
    }
}
