package com.wherecamp.hackathon.phumblr.models;

import android.graphics.Bitmap;

/**
 * Created by Nice Fontaine on 26.11.2015.
 */
public class FlickrImage {

    public String getViews() {
        return views;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }

    private Bitmap image;
    private final String views;
    private final String title;

    public FlickrImage(String views, String title, Bitmap img) {
        this.views = views;
        this.title = title;
        this.image = img;
    }
}
