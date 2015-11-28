package com.wherecamp.hackathon.phumblr.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.wherecamp.hackathon.phumblr.R;
import com.wherecamp.hackathon.phumblr.models.FlickrImage;
import com.wherecamp.hackathon.phumblr.models.Wikipedia;
import com.wherecamp.hackathon.phumblr.network.VolleyRequests;
import com.wherecamp.hackathon.phumblr.utils.ElementParser;
import com.wherecamp.hackathon.phumblr.utils.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements
        VolleyRequests.OnResponseListener,
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FlickrImage.OnImageLoad {

    private static final String TAG =  "MainActivity";

    public static final String SECTION_PATH = "/section";
    public static final String SECTION_TITLE = "title";
    public static final String SECTION_CONTENT = "content";

    public static final String WIKI_PATH = "/wiki";
    public static final String WIKI_TIMESTAMP = "timestamp";
    public static final String WIKI_TITLE = "title";
    public static final String WIKI_ID = "id";
    public static final String WIKI_DISTANCE = "distance";
    public static final String WIKI_SECTION_TITLE = "section_title";
    public static final String WIKI_SECTION_CONTENT = "section_content";

    public static final String FLICKR_PATH = "/flickr";
    public static final String FLICKR_TIMESTAMP = "timestamp";
    public static final String FLICKR_IMAGE = "image";
    public static final String FLICKR_VIEWS = "views";
    public static final String FLICKR_ID = "id";

    public static final String NOTIFICATION_PATH = "/notification";
    public static final String NOTIFICATION_TIMESTAMP = "timestamp";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "content";

    private String url="http://192.168.178.22:8090/live?lat=52.51612&lon=13.37899";

    private ArrayList<FlickrImage> flickrImages = new ArrayList<>();
    private ArrayList<Wikipedia> wikipedias = new ArrayList<>();

    private boolean loading_pics = true;

    private int bitmap_count;

    private GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createToolbar();
        createGoogleApiClient();
        createFloatingActionButton();
    }

    private void createToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void createGoogleApiClient() {
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void createFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                VolleyRequests request = new VolleyRequests(MainActivity.this, url);
                request.makeRequest();

                // test
                String resp = "{\"flickr\":[{\"views\":36081,\"url\":\"https://farm8.staticflickr.com/7147/6642332401_8eff5cb672_n.jpg\",\"photoId\":6642332401},{\"views\":29310,\"url\":\"https://farm3.staticflickr.com/2946/15318111240_e9832c1388_n.jpg\",\"photoId\":15318111240},{\"views\":29283,\"url\":\"https://farm6.staticflickr.com/5598/15504850465_cd9c866280_n.jpg\",\"photoId\":15504850465},{\"views\":28021,\"url\":\"https://farm6.staticflickr.com/5129/5223692318_e97fc7d7e3_n.jpg\",\"photoId\":5223692318},{\"views\":27083,\"url\":\"https://farm4.staticflickr.com/3851/14555097821_15d238bbef_n.jpg\",\"photoId\":14555097821}],\"wiki\":[{\"title\":\"#S_Berlin_Brandenburger_Tor\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"#Radweg_BerlinâKopenhagen\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"Berlin\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"Pariser Platz\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"#Berlin_Brandenburger_Tor\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null}]}";
                ElementParser ep = new ElementParser(resp);

                flickrImages = ep.getFlickrImages();

                bitmap_count = 0;
                for (FlickrImage img : flickrImages) {
                    img.loadBitmap(MainActivity.this);
                }

                //wikipedias = ep.getWikipedias();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected");
        Wearable.DataApi.addListener(googleClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect");
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Wearable.DataApi.removeListener(googleClient, this);
        googleClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onTextResponse(String text) {
        ElementParser ep = new ElementParser(text);
        flickrImages = ep.getFlickrImages();
        wikipedias = ep.getWikipedias();
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        bitmap_count++;
        Log.e("Bitmap count", ""+bitmap_count);
        if (bitmap_count==flickrImages.size()) {
            new SendToDataLayerThread().start();
            Log.e(TAG, "DataMap Send!!!!");
        }
    }

    class SendToDataLayerThread extends Thread {

        public void run() {
            sendNotification();
        }
    }

    private void sendNotification() {
        if (googleClient.isConnected()) {
            Log.e(TAG, "sendNotification()");

            // WIKI
            for (int i=0; i<wikipedias.size(); i++) {

                ArrayList<String[]> sections = wikipedias.get(i).getSections();
                for (int j=0; j<sections.size(); j++) {
                    PutDataMapRequest dataMapRequest = PutDataMapRequest.create(SECTION_PATH);
                    dataMapRequest.getDataMap().putString(SECTION_TITLE, sections.get(j)[0]);
                    dataMapRequest.getDataMap().putString(SECTION_CONTENT, sections.get(j)[1]);
                    PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
                    Wearable.DataApi.putDataItem(googleClient, putDataRequest);
                }

                PutDataMapRequest dataMapRequest = PutDataMapRequest.create(WIKI_PATH);
                dataMapRequest.getDataMap().putDouble(WIKI_TIMESTAMP, System.currentTimeMillis());
                dataMapRequest.getDataMap().putString(WIKI_TITLE, wikipedias.get(i).getTitle());
                dataMapRequest.getDataMap().putString(WIKI_DISTANCE, wikipedias.get(i).getDistance());
                dataMapRequest.getDataMap().putString(WIKI_ID, wikipedias.get(i).getId());
                PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
                Wearable.DataApi.putDataItem(googleClient, putDataRequest);
            }

            // FLICKR
            for (int i=0; i<flickrImages.size(); i++) {
                PutDataMapRequest dataMapRequest = PutDataMapRequest.create(FLICKR_PATH);
                dataMapRequest.getDataMap().putDouble(FLICKR_TIMESTAMP, System.currentTimeMillis());
                dataMapRequest.getDataMap().putString(FLICKR_VIEWS, flickrImages.get(i).getViews());
                dataMapRequest.getDataMap().putString(FLICKR_ID, flickrImages.get(i).getId());
                Asset img = ImageUtils.createAssetFromBitmap(flickrImages.get(i).getBitmap());
                dataMapRequest.getDataMap().putAsset(FLICKR_IMAGE, img);
                PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
                Wearable.DataApi.putDataItem(googleClient, putDataRequest);
            }

            // NOTIFICATION
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(NOTIFICATION_PATH);
            dataMapRequest.getDataMap().putDouble(NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(NOTIFICATION_TITLE, "Interesting Photospot nearby!");
            dataMapRequest.getDataMap().putString(NOTIFICATION_CONTENT, "Swip left and press open to see more...");
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleClient, putDataRequest);

        }
        else {
            Log.e(TAG, "No connection to wearable available!");
        }
    }
}
