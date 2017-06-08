package com.richard.hitball.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Brick extends Cell {
    private static final int BRICK_GAP = 10; //间隙
    private static final float BRICK_BORDER = 5f; //边界

    private int mBlood;

    //砖块颜色
    private static int[] sBloodColors = {
            Color.RED, Color.YELLOW, Color.GREEN
    };

    //定义砖块
    public Brick(int row, int col, int width, int height, int blood) {
        int left = col * width + BRICK_GAP / 2;
        int right = left + width - 3 * BRICK_GAP / 2;
        int top = row * height + BRICK_GAP;
        int bottom = top + height - BRICK_GAP;
        mBody = new Rect(left, top, right, bottom);
        mBlood = blood;
        this.row = row;
        this.col = col;
    }

    //砖块绘制
    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(sBloodColors[mBlood]);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mBody, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mBody.left + BRICK_BORDER,
                mBody.top + BRICK_BORDER,
                mBody.right - BRICK_BORDER,
                mBody.bottom - BRICK_BORDER,
                mPaint);
    }

    //判断砖块数（碰撞则减一）
    @Override
    public boolean hit() {
        mBlood--;
        return mBlood < 0;
    }

    @Override
    public int getBlood() {
        return mBlood;
    }
}
