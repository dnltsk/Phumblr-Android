package com.wherecamp.hackathon.phumblr.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nice Fontaine on 26.11.2015.
 */
public class ImageUtils {

    final private static String TAG = "ImageUtils";

    private static final int TIMEOUT_MS = 10000;

    public ImageUtils() {}

    /**
     * Method crops a circular Bitmap of the squared input Bitmap.
     *
     * @param bitmap Bitmap to be cropped
     * @return bitmap Circular cropped Bitmap
     */
    public static Bitmap getCircularCroppedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = (width > height)? height : width;
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(width /2, height / 2, diameter / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Method to crop a input Bitmap by a specific width and height. The result is a centralized
     * crop Bitmap of the original Bitmap.
     *
     * @param image input Bitmap
     * @param newWidth expected new width as int
     * @param newHeight expected new height as int
     * @return cropped centralized Bitmap
     */
    public static Bitmap getCroppedBitmap(Bitmap image, int newWidth, int newHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        float imageRatio = ((float) width) / ((float) height);
        float cropRatio = ((float) newWidth) / ((float) newHeight);

        Bitmap croppedImage;
        if (cropRatio >= imageRatio) {
            int cropHeight = (width * newHeight) / newWidth;
            croppedImage = Bitmap.createBitmap(image, 0, (height - cropHeight) / 2,
                    width, cropHeight);
        } else {
            int cropWidth = (height * newWidth) / newHeight;
            croppedImage = Bitmap.createBitmap(image, (width - cropWidth) / 2, 0,
                    cropWidth, height);
        }

        return croppedImage;
    }

    /**
     * Method resizes a Bitmap by specified width and height.
     *
     * @param image input Bitmap
     * @param newWidth expected new width as int
     * @param newHeight expected new height as int
     * @return resized Bitmap
     */
    public static Bitmap getResizedBitmap(Bitmap image, int newWidth, int newHeight) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
    }

    /**
     * Method create an Asset of a Bitmap for syncing Handheld and Wearable DataAPI.
     * @param bitmap Bitmap to be converted
     * @return Asset
     */
    public static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }


    public static Bitmap loadBitmapFromAsset(Asset asset, GoogleApiClient googleApiClient) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                googleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
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
}
