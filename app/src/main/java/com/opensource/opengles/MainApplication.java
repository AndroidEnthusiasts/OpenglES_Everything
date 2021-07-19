package com.opensource.opengles;

import android.app.Application;
import android.content.res.Resources;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCore.getInstance().init(this);
    }
}
