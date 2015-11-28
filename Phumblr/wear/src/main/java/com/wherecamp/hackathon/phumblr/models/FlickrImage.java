package com.wherecamp.hackathon.phumblr.models;

import android.graphics.Bitmap;

/**
 * Created by Nice Fontaine on 26.11.2015.
 */
public class FlickrImage {


    public String getViews() {
        return "Image has " + views + " View";
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }


    public String getId() {
        return id;
    }

    private Bitmap image;
    private final String views;
    private final String title;
    private final String id;

    public FlickrImage(String views, String title, Bitmap img, String id) {
        this.views = views;
        this.title = title;
        this.image = img;
        this.id = id;
    }
}
