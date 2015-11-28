package com.wherecamp.hackathon.phumblr.services;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.wherecamp.hackathon.phumblr.models.FlickrImage;
import com.wherecamp.hackathon.phumblr.models.Wikipedia;

import java.util.ArrayList;


public class WearApplication extends Application {

    private static final String TAG = "MyApplication";
    private static WearApplication sInstance;

    private static ArrayList<FlickrImage> images = new ArrayList<>();
    private static ArrayList<Wikipedia> wikis = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Context getAppContext() { return sInstance.getApplicationContext(); }

    public static Resources getResource() { return sInstance.getResources(); }

    public static void setFlickrImages(ArrayList<FlickrImage> images) {
        WearApplication.images = images;
    }

    public static ArrayList<FlickrImage> getFlickrImages() {
        return WearApplication.images;
    }

    public static void setWikis(ArrayList<Wikipedia> wikis) {
        WearApplication.wikis = wikis;
    }

    public static ArrayList<Wikipedia> getWikis() {
        return WearApplication.wikis;
    }
}
