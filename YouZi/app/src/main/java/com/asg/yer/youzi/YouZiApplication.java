package com.asg.yer.youzi;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

/**
 * Created by YER on 17-12-12.
 */
public class YouZiApplication extends Application {

    public Intent getResultIntent() {
        return mResultIntent;
    }

    public void setResultIntent(Intent mResultIntent) {
        this.mResultIntent = mResultIntent;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int mResultCode) {
        this.mResultCode = mResultCode;
    }

    private Intent mResultIntent = null;
    private int mResultCode = 0;

    public MediaProjectionManager getMpmngr() {
        return mMpmngr;
    }

    public void setMpmngr(MediaProjectionManager mMpmngr) {
        this.mMpmngr = mMpmngr;
    }

    private MediaProjectionManager mMpmngr;


    @Override
    public void onCreate() {
        super.onCreate();

    }
}
