package com.laewoong.gooeyeffectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by laewoong on 2018. 3. 17..
 * https://laewoong.github.io
 */

public class GooeyEffectView extends View {

    private Path mLeftCirclePath;
    private Path mRightCirclePath;

    private Paint mPaint;

    private int mCenterX;
    private int mCenterY;

    public GooeyEffectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mLeftCirclePath = new Path();
        mRightCirclePath = new Path();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX = w/2;
        mCenterY = h/2;

        initGooeyPath();
    }

    public void initGooeyPath() {

        mLeftCirclePath.reset();
        mRightCirclePath.reset();

        final float RADIUS = 100f;
        final float GAP = 75f;

        mLeftCirclePath.addCircle((mCenterX - GAP), mCenterY, RADIUS, Path.Direction.CW);
        mRightCirclePath.addCircle((mCenterX + GAP), mCenterY, RADIUS, Path.Direction.CW);
        mLeftCirclePath.op(mRightCirclePath, Path.Op.UNION);

        final float SEGMENT_NUM = 20f;
        PathMeasure pm = new PathMeasure(mLeftCirclePath, true);
        DiscretePathEffect discretePathEffect= new DiscretePathEffect(pm.getLength()/SEGMENT_NUM, 0);

        CornerPathEffect cornerPathEffect = new CornerPathEffect(50);

        ComposePathEffect pathEffect = new ComposePathEffect(cornerPathEffect, discretePathEffect);

        mPaint.setPathEffect( pathEffect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mLeftCirclePath, mPaint);
    }
}
