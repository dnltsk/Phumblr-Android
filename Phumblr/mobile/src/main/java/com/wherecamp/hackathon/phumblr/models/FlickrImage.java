package com.wherecamp.hackathon.phumblr.models;

import android.graphics.Bitmap;

import com.wherecamp.hackathon.phumblr.network.VolleyRequests;

/**
 * Created by Nice Fontaine on 26.11.2015.
 */
public class FlickrImage implements
        VolleyRequests.OnResponseListener {


    public String getUrl() {
        return url;
    }

    public String getViews() {
        return views;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private final String url;
    private final String views;
    private Bitmap bitmap;
    private boolean hasBitmap = false;


    public FlickrImage(String url, String views) {
        this.url = url;
        this.views = views;

        fetchImage();
    }

    private void fetchImage() {
        VolleyRequests request = new VolleyRequests(this, url);
        request.makeImageRequest();
    }

    @Override
    public void onTextResponse(String text) {

    }

    @Override
    public void onImageResponse(Bitmap bitmap) {
        this.bitmap = bitmap;
        hasBitmap = true;
    }

    public boolean hasBitmap() {
        return hasBitmap;
    }
}
