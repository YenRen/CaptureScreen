package com.asg.yer.youzi.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.asg.yer.youzi.MainActivity;
import com.asg.yer.youzi.R;
import com.asg.yer.youzi.YouZiApplication;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * 截图Service
 * Created by YER on 17/12/14.
 */
public class CaptureService extends Service {

    private static final String TAG = "CService";

    private MediaProjectionManager mMpmngr;
    private MediaProjection mMpj;
    private ImageReader mImageReader;
    private String mImageName;
    private String mImagePath;
    private int screenDensity;
    private int windowWidth;
    private int windowHeight;
    private VirtualDisplay mVirtualDisplay;
    private WindowManager wm;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotitycation();
        createEnvironment();
        createSceenShot();
    }


    private void createNotitycation() {
        Notification notification = new Notification(R.mipmap.ic_launcher, "zhou",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (Build.VERSION.SDK_INT <16) {
            Class clazz = notification.getClass();
            try {
                Method m2 = clazz.getDeclaredMethod("setLatestEventInfo", Context.class,CharSequence.class,CharSequence.class,PendingIntent.class);
                m2.invoke(notification, getApplication(), "标题","内容", pendingIntent);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        } else {
            notification = new Notification.Builder(getApplication())
                    .setAutoCancel(true)
                    .setContentTitle("标题")
                    .setContentText("内容")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .build();
        }
        startForeground(0x1982, notification);
    }


    private void createEnvironment() {
        mImagePath = Environment.getExternalStorageDirectory().getPath() + "/screenshort/";
        mMpmngr = ((YouZiApplication) getApplication()).getMpmngr();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowWidth = wm.getDefaultDisplay().getWidth();
        windowHeight = wm.getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenDensity = displayMetrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2);

    }

    private void createSceenShot() {

                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG, "start startVirtual");
//                        startVirtual();
//                    }
//                }, 500);
//                // Handler handler1 = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG, "start startCapture");
//                        startCapture();
//                    }
//                }, 1000);
//                // Handler handler2 = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG, "start stopVirtual");
//                        mCaptureIv.setVisibility(View.VISIBLE);
//                        stopVirtual();
//                    }
//                }, 1500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startVirtual();
            }
        }, 0);


                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {

//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.e(TAG, "start startCapture");
//                                        startCapture();
//                                    }
//                                }, 200);


                                try {
                                    sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                startCapture();

                            }
                        },0,500/* 表示0毫秒之後，每隔200毫秒執行一次 */);





    }

    private void stopVirtual() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }

    private void startCapture() {
        mImageName = System.currentTimeMillis() + ".png";
        Log.e(TAG, "image name is : " + mImageName);
        Image image = mImageReader.acquireLatestImage();
        if(image!=null) {
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
//            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
//            bitmap.copyPixelsFromBuffer(buffer);
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
//
//
//            bitmap.isRecycled();
            image.close();
            Log.e(TAG, "file save success ! are you ok");
        }
//        if (bitmap != null) {
//            Log.e(TAG, "bitmap  create success ");
//            try {
//                File fileFolder = new File(mImagePath);
//                if (!fileFolder.exists())
//                    fileFolder.mkdirs();
//                File file = new File(mImagePath, mImageName);
//                if (!file.exists()) {
//                    Log.e(TAG, "file create success ");
//                    file.createNewFile();
//                }
//                FileOutputStream out = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                out.flush();
//                out.close();
//                Log.e(TAG, "file save success ");
//                Toast.makeText(this.getApplicationContext(), "截图成功", Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                Log.e(TAG, e.toString());
//                e.printStackTrace();
//            }
//        }
    }

    private void startVirtual() {
        if (mMpj != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    private void setUpMediaProjection() {
        int resultCode = ((YouZiApplication) getApplication()).getResultCode();
        Intent data = ((YouZiApplication) getApplication()).getResultIntent();
        if(mMpmngr==null){ //时间跑久了  mMpmngr会被回收
            mMpmngr = ((YouZiApplication) getApplication()).getMpmngr();
        }
        mMpj = mMpmngr.getMediaProjection(resultCode, data);
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMpj.createVirtualDisplay("capture_screen", windowWidth, windowHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVirtual();
        if (mMpj != null) {
            mMpj.stop();
            mMpj = null;
        }
    }
}
