package com.richard.hitball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.richard.hitball.entity.Ball;
import com.richard.hitball.entity.Bat;
import com.richard.hitball.entity.Table;

/**
 * 第一步：获得传感器管理器
 * 第二步：为具体的传感器注册监听器
 * 第三步：实现具体的监听方法
 *  public  void onSensorChanged(SensorEvent event) {}
    public void onAccuracyChanged(Sensor sensor ,int accuracy ){}
 */

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback,
        SensorEventListener {
    //游戏状态常量
    public static int STATE_READY = 1;
    public static int STATE_PLAY = 2;
    public static int STATE_PASS = 3;
    public static int STATE_OVER = 4;

    private int mState;

    private Table mTable;
    private Ball mBall;
    private Bat mBat;

    private boolean mIsRunning;
    private GestureDetector mGestureDetector;
    private SensorManager mSensorManager;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        //实例化手势识别对象
        mGestureDetector = new GestureDetector(getContext(), new GameGestureDetector());
        //实例化传感器管理对象
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        //设置旋转向量传感器
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        //监听传感器改变的采样率是否为适合游戏的速率
        boolean ok = mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        Log.d("mytag", "ok = " + ok);
        Log.d("mytag", "sensor = " + sensor);

        //实例化窗口管理器
        WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);

        //实例化矩形
        Rect screenRect = new Rect();

        windowManager.getDefaultDisplay().getRectSize(screenRect);

        mTable = new Table(context, screenRect);
        mBall = new Ball();
        mTable.setBall(mBall);
        mBat = new Bat();
        mTable.setBat(mBat);
        mTable.reset();
        mState = STATE_READY;
    }

    //判断触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //单点触碰
            case MotionEvent.ACTION_MOVE:   //触摸点移动动作
                if (mIsRunning && mState == STATE_PLAY) {
                    //当状态为STATE_PLAY球开始移动（mIsRunning  mState同为1）
                    mTable.startBatMove(event);
                }
                break;
            case MotionEvent.ACTION_UP: //单点触摸离开动作
                mTable.stopBatMove();
                break;
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    //监听传感器的值变化
    @Override
    public void onSensorChanged(SensorEvent event) {
        //
        if (!mIsRunning || mState != STATE_PLAY) return;
        int sensorType = event.sensor.getType();    //存储传感器类型
        float[] rotationMatrix;
        switch (sensorType) {
            case Sensor.TYPE_ROTATION_VECTOR:   //为旋转矢量传感器（代表设备的方向）
                rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                float[] orientationValues = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientationValues);
                //倾斜度获取
                double pitch = Math.toDegrees(orientationValues[1]);
                double roll = Math.toDegrees(orientationValues[2]);
                Log.e("mytag", "pitch = " + pitch + ", roll = " + roll);

                //球板移动
                mTable.startBatMove(roll);
                //改变球板大小
                mTable.changeBatBody(pitch);
                break;

        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mTable.draw(canvas);
        if (mTable.isBallOutside()) {
            //游戏结束
            mState = STATE_OVER;
            mTable.showGameOver();
        } else if (mTable.hasNoneBrick()) {
            //通关
            mState = STATE_PASS;
            mTable.showGamePass();
        }
    }

    @Override
    public void run() {
        while (mIsRunning) {
            Canvas canvas = getHolder().lockCanvas();
            synchronized (mTable) {
                draw(canvas);
            }
            getHolder().unlockCanvasAndPost(canvas);
            sleep(20);
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRunning = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRunning = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class GameGestureDetector extends GestureDetector.SimpleOnGestureListener {
        //点击界面，游戏开始或重置
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mIsRunning) {
                synchronized (mTable) {
                    if (mState == STATE_READY) {
                        mTable.shotBall();
                        mState = STATE_PLAY;
                    } else if (mState == STATE_OVER || mState == STATE_PASS) {
                        mState = STATE_READY;
                        //界面重置
                        mTable.reset();
                    }
                }
            }
            return true;
        }
    }
}
