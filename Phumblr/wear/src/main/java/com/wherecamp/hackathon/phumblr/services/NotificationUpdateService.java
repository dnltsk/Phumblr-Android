package com.wherecamp.hackathon.phumblr.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.wherecamp.hackathon.phumblr.R;
import com.wherecamp.hackathon.phumblr.activities.MainActivity;
import com.wherecamp.hackathon.phumblr.models.FlickrImage;
import com.wherecamp.hackathon.phumblr.models.Wikipedia;
import com.wherecamp.hackathon.phumblr.utils.ImageUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;

public class NotificationUpdateService extends WearableListenerService implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "NotificationUpdate";

    private static final int TIMEOUT_MS = 10000;

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
    public static final String ACTION_DISMISS = "com.wherecamp.hackathon.phumblr.DISMISS";

    private static ArrayList<FlickrImage> flickr_images = new ArrayList<>();
    private static ArrayList<Wikipedia> wikis = new ArrayList<>();
    private static ArrayList<String[]> sections = new ArrayList<>();

    private GoogleApiClient googleApiClient;
    private int notificationId = 001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (ACTION_DISMISS.equals(action)) {
                dismissNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.e(TAG, "onDataChanged()");

        if (flickr_images.size()>5) {
            flickr_images = new ArrayList<>();
        }

        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if (SECTION_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String title = dataMapItem.getDataMap().getString(SECTION_TITLE);
                    String content = dataMapItem.getDataMap().getString(SECTION_TITLE);
                    Log.e("WIKI________TITLE", title);
                    sections.add(new String[]{title, content});
                }
            }
        }

        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if (FLICKR_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    Bitmap img = loadBitmapFromAsset(dataMapItem.getDataMap().getAsset(FLICKR_IMAGE));
                    String views = dataMapItem.getDataMap().getString(FLICKR_VIEWS);
                    String id = dataMapItem.getDataMap().getString(FLICKR_ID);
                    flickr_images.add(new FlickrImage(views, "Phumblr #"+id, img, id));
                }
            }
        }

        if (wikis.size()>5) {
            wikis = new ArrayList<>();
        }

        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if (WIKI_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String title = dataMapItem.getDataMap().getString(WIKI_TITLE);
                    Log.e("WIKI________TITLE", title);
                    String distance = dataMapItem.getDataMap().getString(WIKI_DISTANCE);
                    wikis.add(new Wikipedia(title, distance, sections));
                    sections = new ArrayList<>();
                }
            }
        }

        WearApplication.setFlickrImages(flickr_images);

        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if (NOTIFICATION_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String title = dataMapItem.getDataMap().getString(NOTIFICATION_TITLE);
                    String content = dataMapItem.getDataMap().getString(NOTIFICATION_CONTENT);
                    sendNotification(title, content);
                }
            }
        }
    }


    public Bitmap loadBitmapFromAsset(Asset asset) {

        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        ConnectionResult result = googleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }

        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                googleApiClient, asset).await().getInputStream();
        googleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);

    }

    private void sendNotification(String title, String content) {

        // this intent will open the activity when the user taps the "open" action on the notification
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingViewIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // this intent will be sent when the user swipes the notification to dismiss it
        Intent dismissIntent = new Intent(ACTION_DISMISS);
        PendingIntent pendingDeleteIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setDeleteIntent(pendingDeleteIntent)
                .setContentIntent(pendingViewIntent);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId++, notification);
    }

    private void dismissNotification() {
        new DismissNotificationCommand(this).execute();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected");
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect");
    }

    private class DismissNotificationCommand implements
            GoogleApiClient.ConnectionCallbacks,
            ResultCallback<DataApi.DeleteDataItemsResult>,
            GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "DismissNotification";

        private final GoogleApiClient mGoogleApiClient;

        public DismissNotificationCommand(Context context) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        public void execute() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            final Uri dataItemUri =
                    new Uri.Builder().scheme(WEAR_URI_SCHEME).path(NOTIFICATION_PATH).build();
            Wearable.DataApi.deleteDataItems(
                    mGoogleApiClient, dataItemUri).setResultCallback(this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
        }

        @Override
        public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
            if (!deleteDataItemsResult.getStatus().isSuccess()) {
                Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
            }
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed");
        }
    }
}
