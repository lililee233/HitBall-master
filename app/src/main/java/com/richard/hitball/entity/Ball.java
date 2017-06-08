package com.richard.hitball.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Ball {
    private static final float RADIUS = 30.0f;
    private static final int INIT_POS_CX = 0;
    private static final int INIT_POS_CY = 0;
    private static final int DEFAULT_SPEED = 1;
    private Paint mPaint;

    private Point mCenter;

    private float mRadius;

    private Point mSpeed;

    public Point getCenter() {
        return mCenter;
    } //获取中心位置

    public float getRadius() {
        return mRadius;
    } //获取半径

    public boolean isToRight() {
        return mSpeed.x > 0;
    } //判断是否靠右

    public boolean isToLeft() {
        return mSpeed.x < 0;
    }

    public boolean isToTop() {
        return mSpeed.y < 0;
    }

    public boolean isToBottom() {
        return mSpeed.y > 0;
    }

    public void reverseYSpeed() {
        mSpeed.y = -mSpeed.y;
    } //重置移动方向

    public void reverseXSpeed() {
        mSpeed.x = -mSpeed.x;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    //绘制球体
    public Ball() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);

        mCenter = new Point(INIT_POS_CX, INIT_POS_CY);
        mRadius = RADIUS;
        mSpeed = new Point(0, 0);
    }

    //发射坐标
    public void shot(int x, int y) {
        mSpeed.x = x;
        mSpeed.y = y;
    }

    //绘制圆
    public void draw(Canvas canvas) {
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius, mPaint);
        mCenter.offset(mSpeed.x, mSpeed.y);
    }

    //设置绘制位置
    public void setPosition(int x, int y) {
        mCenter.x = x;
        mCenter.y = y;
    }

    public void stop() {
        mSpeed.x = 0;
        mSpeed.y = 0;
    }
}
