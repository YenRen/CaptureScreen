package com.asg.yer.youzi.window;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.asg.yer.youzi.R;
import com.asg.yer.youzi.manager.YzWindowManager;
import com.asg.yer.youzi.service.YzWindowService;


public class SusDetailWindow extends LinearLayout {

    // 记录大悬浮窗的宽度和高度
    public static int viewWidth;
    public static int viewHeight;

    public SusDetailWindow(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        Button close = (Button) findViewById(R.id.close);
        Button back = (Button) findViewById(R.id.back);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                YzWindowManager.removeBigWindow(context);
                YzWindowManager.removeSmallWindow(context);
                Intent intent = new Intent(getContext(), YzWindowService.class);
                context.stopService(intent);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                YzWindowManager.removeBigWindow(context);
                YzWindowManager.createSmallWindow(context);
            }
        });
    }
}
