package com.wherecamp.hackathon.phumblr.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.wherecamp.hackathon.phumblr.R;
import com.wherecamp.hackathon.phumblr.managers.MyApplication;


public class VolleyRequests {

    private static final String TAG = "VolleyRequest";

    private OnResponseListener mCallback;
    private String url;
    private int MY_SOCKET_TIMEOUT_MS = 10000;

    public interface OnResponseListener {
        void onTextResponse(String text);
        void onImageResponse(Bitmap bitmap);
    }

    public VolleyRequests(Object object, String url) {
        this.mCallback = (OnResponseListener) object;
        this.url = url;
    }

    public void makeRequest() {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Log.d(TAG, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                mCallback.onTextResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    public void makeImageRequest() {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Log.d(TAG, url);
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap bitmap) {
                       mCallback.onImageResponse(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Bitmap bm = BitmapFactory.decodeResource(
                                MyApplication.getResource(), R.drawable.no_image);
                        mCallback.onImageResponse(bm);
                    }
                });

        requestQueue.add(request);
    }
}
