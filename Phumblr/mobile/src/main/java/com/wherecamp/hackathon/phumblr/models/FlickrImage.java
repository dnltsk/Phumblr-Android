package com.wherecamp.hackathon.phumblr.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wherecamp.hackathon.phumblr.managers.MyApplication;
import com.wherecamp.hackathon.phumblr.network.VolleyRequests;


/**
 * Created by Nice Fontaine on 26.11.2015.
 */
public class FlickrImage implements
        VolleyRequests.OnResponseListener {

    private static final String TAG = "FlickrImage";

    public String getId() {
        return id;
    }

    public String getViews() {
        return views;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private final String id;
    private final String url;
    private final String views;
    private Bitmap bitmap;
    private Target loadtarget;

    private OnImageLoad mCallback;

    private boolean hasBitmap = false;


    public interface OnImageLoad {
        void onImageLoaded(Bitmap bitmap);
    }


    public FlickrImage(String url, String views, String id) {
        this.url = url;
        this.views = views;
        this.id = id;
    }


    public void loadBitmap(Object object) {

        mCallback = (OnImageLoad) object;

        loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                handleLoadedBitmap(bitmap);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
        };

        Picasso.with(MyApplication.getAppContext()).load(url).into(loadtarget);
    }

    public void handleLoadedBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        Log.e(id, "has image from "+url);
        mCallback.onImageLoaded(bitmap);
    }

    @Override
    public void onTextResponse(String text) {

    }

    public boolean hasBitmap() {
        return bitmap!=null;
    }
}
