package com.wherecamp.hackathon.phumblr.utils;

import android.util.Log;

import com.wherecamp.hackathon.phumblr.models.FlickrImage;
import com.wherecamp.hackathon.phumblr.models.Wikipedia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nice Fontaine on 26.11.2015.
 */
public class ElementParser {


    private static final String TAG = "ElementParser";

    private final String FLICKR_TAG = "flickr";
    private final String URL_TAG = "url";
    private final String VIEWS_TAG = "views";
    private final String PHOTO_ID_TAG = "photoId";

    private final String WIKI_TAG = "wiki";
    private final String TITLE_TAG = "title";
    private final String DISTANCE_TAG = "distanceFromHotspotInMeter";
    private final String WIKI_ID_TAG = "wikiId";

    private final String SEC_TITLE_TAG = "section_title";
    private final String SECTIONS_TAG = "sections";
    private final String CONTENT_TAG = "content";

    public ArrayList<FlickrImage> getFlickrImages() {
        return flickrImages;
    }

    public ArrayList<Wikipedia> getWikipedias() {
        return wikipedias;
    }

    private ArrayList<FlickrImage> flickrImages = new ArrayList<>();
    private ArrayList<Wikipedia> wikipedias = new ArrayList<>();


    public ElementParser(String response) {
        Log.d(TAG, "Response: > " + response);
        if (response != null) {
            parse(response);
        } else {
            Log.e(TAG, "Couldn't get any data from the url");
        }
    }

    private void parse(String response) {

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray imgs = jsonObj.getJSONArray(FLICKR_TAG);
            JSONArray wiks = jsonObj.getJSONArray(WIKI_TAG);

            for (int i=0; i<imgs.length(); i++) {
                JSONObject im_js = (JSONObject) imgs.get(i);
                String url = im_js.getString(URL_TAG);
                String views = im_js.getString(VIEWS_TAG);
                String id = im_js.getString(PHOTO_ID_TAG);
                flickrImages.add(new FlickrImage(url, views, ""+(i+1)));
            }


            for (int i=0; i<wiks.length(); i++) {
                JSONObject wk_js = (JSONObject) wiks.get(i);
                String title = wk_js.getString(TITLE_TAG);
                String dist = wk_js.getString(DISTANCE_TAG);
                //String wiki_id = wk_js.getString(WIKI_ID_TAG);
                JSONArray secs = wk_js.getJSONArray(SECTIONS_TAG);

                ArrayList<String[]> sections = new ArrayList<>();

                for (int j=0; j<secs.length(); j++) {
                    JSONObject section = (JSONObject) secs.get(j);
                    String section_title = section.getString(TITLE_TAG);
                    String content = section.getString(CONTENT_TAG);
                    sections.add(new String[]{section_title, content});
                }

                wikipedias.add(new Wikipedia(title, dist, "", sections));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
