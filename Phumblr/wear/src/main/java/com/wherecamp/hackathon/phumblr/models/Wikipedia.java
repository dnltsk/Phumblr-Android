package com.wherecamp.hackathon.phumblr.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nice Fontaine on 26.11.2015.
 */
public class Wikipedia {

    public String getTitle() {
        return title;
    }

    public String getDistance() {
        return distance;
    }

    public ArrayList<String[]> getSections() {
        return sections;
    }

    private final String title;
    private final String distance;
    private final ArrayList<String[]> sections;

    public Wikipedia(String title, String distance, ArrayList<String[]> sections) {
        this.title = title;
        this.distance = distance;
        this.sections = sections;
    }
}
