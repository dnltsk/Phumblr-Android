package com.wherecamp.hackathon.phumblr.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wherecamp.hackathon.phumblr.managers.MyApplication;


public class VolleySingleton {

    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static VolleySingleton getInstance() {

        if (sInstance == null) {
            sInstance = new VolleySingleton();
        }

        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
