package com.wherecamp.hackathon.phumblr.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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


public class MainActivity extends AppCompatActivity implements
        VolleyRequests.OnResponseListener,
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FlickrImage.OnImageLoad {

    private static final String TAG =  "MainActivity";

    private static final int SIGNAL_TIME = 5000;

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

    private String url_head="http://141.64.171.114:8090/live?lat=";

    private ArrayList<FlickrImage> flickrImages = new ArrayList<>();
    private ArrayList<Wikipedia> wikipedias = new ArrayList<>();

    private LocationManager locationManager;
    private Location location;

    private int bitmap_count;

    private GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createToolbar();
        createGoogleApiClient();
        setSensorManagement();
        startSniffingAround();
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

    /**
     * Method sets LocationManager, User-Location, SensorManager and the Sensors.
     */
    private void setSensorManagement() {
        /* Location */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        registerSensors();
    }

    /**
     * Method registers Location and Internal Sensors, particularly the Accelerometer,
     * the Magnetometer with the SensorManager and GPS and WiFi with the LocationManager.
     */
    private void registerSensors() {
        // GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                SIGNAL_TIME, 10, locationListener);
        // WiFi
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                SIGNAL_TIME, 10, locationListener);
    }


    private void createFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                double lat = 52.51612;
                double lon = 13.37899;
                String url = url_head + lat + "&lon=" + lon;
                VolleyRequests request = new VolleyRequests(MainActivity.this, url);
                request.makeRequest();

                // test
                //String resp = "{\"flickr\":[{\"views\":36081,\"url\":\"https://farm8.staticflickr.com/7147/6642332401_8eff5cb672_n.jpg\",\"photoId\":6642332401},{\"views\":29310,\"url\":\"https://farm3.staticflickr.com/2946/15318111240_e9832c1388_n.jpg\",\"photoId\":15318111240},{\"views\":29283,\"url\":\"https://farm6.staticflickr.com/5598/15504850465_cd9c866280_n.jpg\",\"photoId\":15504850465},{\"views\":28021,\"url\":\"https://farm6.staticflickr.com/5129/5223692318_e97fc7d7e3_n.jpg\",\"photoId\":5223692318},{\"views\":27083,\"url\":\"https://farm4.staticflickr.com/3851/14555097821_15d238bbef_n.jpg\",\"photoId\":14555097821}],\"wiki\":[{\"title\":\"#S_Berlin_Brandenburger_Tor\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"#Radweg_BerlinâKopenhagen\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"Berlin\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"Pariser Platz\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null},{\"title\":\"#Berlin_Brandenburger_Tor\",\"distanceFromHotspotInMeter\":0.0,\"wikiId\":null}]}";
                //ElementParser ep = new ElementParser(resp);

                //flickrImages = ep.getFlickrImages();

                //bitmap_count = 0;
                //for (FlickrImage img : flickrImages) {
                //    img.loadBitmap(MainActivity.this);
                //}

                //wikipedias = ep.getWikipedias();
            }
        });
    }

    private void startSniffingAround() {

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = new Location("default");

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
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onTextResponse(String text) {
        if (!text.equals("{}")) {
            ElementParser ep = new ElementParser(text);
            flickrImages = ep.getFlickrImages();
            bitmap_count = 0;
            for (FlickrImage img : flickrImages) {
                img.loadBitmap(MainActivity.this);
            }
            wikipedias = ep.getWikipedias();
        }
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
            // TODO: test to real
            // createFloatingActionButton()
            // here
            // ElementParser.parse()

            // WIKI
            for (int i=0; i<wikipedias.size(); i++) {

                ArrayList<String[]> sections = wikipedias.get(i).getSections();
                for (int j=0; j<sections.size(); j++) {
                    PutDataMapRequest dataMapRequest = PutDataMapRequest.create(SECTION_PATH);
                    dataMapRequest.getDataMap().putString(SECTION_TITLE, sections.get(j)[0]);
                    dataMapRequest.getDataMap().putString(SECTION_CONTENT, sections.get(j)[1]);
                    Log.e("section", sections.get(j)[0]);
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

    /**
     * LocationListener is receiving notifications from the LocationManager when the location
     * has changed. The Interface methods are called if the LocationListener has been registered
     * with the LocationManager service.
     */
    private LocationListener locationListener = new LocationListener() {

        /**
         * callback method (asynchronous) when user position changed.
         * @param   current  Location object containing latitude and longitude in degrees
         */
        @Override
        public void onLocationChanged(Location current) {
            location = current;
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            String url = url_head + lat + "&lon=" + lon;
            Log.e(TAG, url);
            VolleyRequests request = new VolleyRequests(MainActivity.this, url);
            request.makeRequest();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
}
