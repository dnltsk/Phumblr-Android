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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        VolleyRequests.OnResponseListener,
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String NOTIFICATION_PATH = "/notification";
    public static final String NOTIFICATION_TIMESTAMP = "timestamp";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "content";
    private static final String TAG =  "MainActivity";

    private String url="http://192.168.178.22:8090/live?lat=52.51612&lon=13.37899";

    private ArrayList<FlickrImage> flickrImages = new ArrayList<>();
    private ArrayList<Wikipedia> wikipedias = new ArrayList<>();

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

                new SendToDataLayerThread().start();

                Snackbar.make(view, "DataMap Send!!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    protected void onResume() {
        super.onResume();
        googleClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    public void onImageResponse(Bitmap bitmap) {
    }

    class SendToDataLayerThread extends Thread {

        public void run() {
            sendNotification();
        }
    }

    private void sendNotification() {
        if (googleClient.isConnected()) {
            Log.e(TAG, "sendNotification()");
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(NOTIFICATION_PATH);
            dataMapRequest.getDataMap().putDouble(NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(NOTIFICATION_TITLE, "This is the title");
            dataMapRequest.getDataMap().putString(NOTIFICATION_CONTENT, "This is a notification with some text.");
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleClient, putDataRequest);
        }
        else {
            Log.e(TAG, "No connection to wearable available!");
        }
    }
}
