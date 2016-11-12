package com.gokselpirnal.dialupview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by gokselpirnal on 12/11/2016.
 */

public class DialUpView extends View {

    private Paint backgroundPaint;
    private Paint centerPaint;
    private Paint numberBackgroundPaint;
    private Paint textPaint;

    private Rect textBounds = new Rect();

    PointF triggerOutPosition,triggerInPosition;

    private int full, half, quarter;

    private float startAngle = 0;
    private float currentAngle = 0;

    private boolean isBackAnimationActive = false;

    public DialUpView(Context context) {
        super(context);
        init();
    }

    public DialUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DialUpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.parseColor("#333333"));

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(Color.parseColor("#aaaaaa"));

        numberBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberBackgroundPaint.setStyle(Paint.Style.FILL);
        numberBackgroundPaint.setColor(Color.parseColor("#FFFFFF"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(70f);
        textPaint.setColor(Color.parseColor("#333333"));

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec < heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }

        triggerOutPosition = getPosition(half, half, half, 45);
        triggerInPosition = getPosition(half, half, quarter * 1.2f, 45);

        full = getMeasuredWidth();
        half = full / 2;
        quarter = full / 4;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(half, half, half, backgroundPaint);
        canvas.drawCircle(half, half, quarter * 1.2f, centerPaint);

        for (int i = 0; i < 10; i++) {
            PointF pointF = getPosition(half, half, half - (half - quarter * 1.2f) / 2, 90 + (currentAngle - startAngle) + 300 / 10 * (9 - i));

            canvas.drawCircle(pointF.x, pointF.y, (half - quarter * 1.3f) / 2, numberBackgroundPaint);

            textPaint.getTextBounds(String.valueOf(i == 9 ? 0 : i + 1), 0, 1, textBounds);

            canvas.drawText(String.valueOf(i == 9 ? 0 : i + 1), pointF.x - textBounds.width() / 2, pointF.y + textBounds.height() / 3, textPaint);
        }


        if (isBackAnimationActive) {
            this.postInvalidateDelayed(100 / 60);
            this.currentAngle = (this.currentAngle - 1) % 360;
            this.isBackAnimationActive = currentAngle != 0;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (!this.isBackAnimationActive) {
                    this.startAngle = 0;
                    this.currentAngle = 0;
                    this.isBackAnimationActive = false;

                    if (getDistanceToCenter(event.getX(), event.getY()) > quarter * 1.2f && getDistanceToCenter(event.getX(), event.getY()) < half) {
                        this.startAngle = (float) (Math.toDegrees(Math.atan2(getMeasuredHeight() / 2 - event.getY(), half - event.getX())));
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (!this.isBackAnimationActive) {
                    if (getDistanceToCenter(event.getX(), event.getY()) > quarter * 1.2f && getDistanceToCenter(event.getX(), event.getY()) < half) {
                        this.currentAngle = (float) Math.floor(Math.toDegrees(Math.atan2(getMeasuredHeight() / 2 - event.getY(), half - event.getX())));
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                this.isBackAnimationActive = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                if (Math.max(currentAngle, startAngle) - Math.min(currentAngle, startAngle) < 10) {
                    this.currentAngle = 0;
                    this.startAngle = 0;
                    this.isBackAnimationActive = true;
                    invalidate();
                    break;
                }

                this.currentAngle = (float) Math.floor(currentAngle - startAngle);
                this.startAngle = 0;

                this.isBackAnimationActive = true;
                invalidate();
                break;
        }

        return true;

    }


    private PointF getPosition(int x, int y, float radius, float angle) {
        return new PointF((float) (x + radius * Math.cos(Math.toRadians(angle))), (float) (y + radius * Math.sin(Math.toRadians(angle))));
    }

    private double getDistanceToCenter(float touchedX, float touchedY) {
        return Math.sqrt(Math.pow(touchedX - half, 2) + Math.pow(touchedY - half, 2));
    }
}
