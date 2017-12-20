package com.asg.yer.youzi.window;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.asg.yer.youzi.R;
import com.asg.yer.youzi.manager.YzWindowManager;

import java.lang.reflect.Field;

public class SusWindow extends LinearLayout {
    //屏宽和屏高
    public static int screenWidth;
    public static int screenHeight;

    // 记录小悬浮窗的宽度 和 高度
    public static int viewWidth;
    public static int viewHeight;

    // 记录系统状态栏的高度
    private static int statusBarHeight;

    // 用于更新小悬浮窗的位置
    private WindowManager windowManager;

    // 小悬浮窗的参数
    private WindowManager.LayoutParams mParams;

    // 记录当前手指位置在屏幕上的横坐标值和纵坐标值
    private float xInScreen;
    private float yInScreen;

    // 记录手指按下时在屏幕上的横坐标值和纵坐标值
    private float xDownInScreen;
    private float yDownInScreen;

    // 记录手指按下时在小悬浮窗的View上的横坐标值和纵坐标值
    private float xInView;
    private float yInView;

    public SusWindow(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;     // 屏幕宽度（像素）
//        screenHeight = metric.heightPixels;   // 屏幕高度（像素）

        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view  = findViewById(R.id.small_window_layout);
        viewWidth  = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        TextView percentView = (TextView) findViewById(R.id.percent);
        percentView.setText(YzWindowManager.getUsedPercentValue(context));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                Log.d("sam", "onTouchEvent: 按下：" + xInScreen + " ; " + yInScreen);
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                Log.d("sam", "onTouchEvent: Move：" + xInScreen + " ; " + yInScreen);
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if(Math.abs(xDownInScreen -  xInScreen)<10 && Math.abs(yDownInScreen - yInScreen)<10){
                    openBigWindow();
                }
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。有些机型不可以 所以用上方法
//                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
//                    openBigWindow();

                //将浮窗贴边
                if(2*mParams.x>screenWidth){
                    mParams.x = screenWidth - viewWidth;
                    windowManager.updateViewLayout(this, mParams);
                }else {
                    mParams.x = 0;
                    windowManager.updateViewLayout(this, mParams);
                }

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
        YzWindowManager.createBigWindow(getContext());
        YzWindowManager.removeSmallWindow(getContext());
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

}
