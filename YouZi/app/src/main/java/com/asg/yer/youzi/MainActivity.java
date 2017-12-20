package com.asg.yer.youzi;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.asg.yer.youzi.manager.YzWindowManager;
import com.asg.yer.youzi.screenshot.ScreenShotActivity;
import com.asg.yer.youzi.service.CaptureService;
import com.asg.yer.youzi.service.YzWindowService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YER on 17-12-12.
 */
public class MainActivity extends AppCompatActivity {

    private MediaProjectionManager mMpMngr;
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private Intent mResultIntent = null;
    private int mResultCode = 0;
    public static final String TAG = "MainAc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMpMngr = (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mResultIntent = ((YouZiApplication) getApplication()).getResultIntent();
        mResultCode = ((YouZiApplication) getApplication()).getResultCode();
    }

    /*
     *开启浮窗
     */
    public void startFloat(View view){
        Intent intent = new Intent(MainActivity.this, YzWindowService.class);
        startService(intent);
        this.finish();
    }

    /*
     *关闭浮窗
     */
    public void stopFloat(View view){
        YzWindowManager.removeBigWindow(this);
        YzWindowManager.removeSmallWindow(this);
        Intent intent = new Intent(this, YzWindowService.class);
        stopService(intent);
    }

    /*
     * 连续截图
     * @param view
     */
    public void screenShot(View view){
//        startActivity(new Intent(MainActivity.this, ScreenShotActivity.class));
        startIntent();
        stopService(new Intent(getApplicationContext(),  CaptureService.class ));
    }

    /*
     * 截图一张图
     * @param view
     */
    public void screenOneShot(View view){
        startActivity(new Intent(MainActivity.this, ScreenShotActivity.class));
    }


    private void startIntent() {
        if (mResultIntent != null && mResultCode != 0) {
            startService(new Intent(getApplicationContext(),CaptureService.class));
        } else {
            startActivityForResult(mMpMngr.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG,"get capture permission success!");
                mResultCode = resultCode;
                mResultIntent = data;
                ((YouZiApplication) getApplication()).setResultCode(resultCode);
                ((YouZiApplication) getApplication()).setResultIntent(data);
                ((YouZiApplication) getApplication()).setMpmngr(mMpMngr);
                startService(new Intent(getApplicationContext(),CaptureService.class));
            }
        }
    }

}
