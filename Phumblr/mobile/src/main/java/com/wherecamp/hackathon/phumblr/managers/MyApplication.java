package com.wherecamp.hackathon.phumblr.managers;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;


public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Context getAppContext() { return sInstance.getApplicationContext(); }

    public static Resources getResource() { return sInstance.getResources(); }
}
