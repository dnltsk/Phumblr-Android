package com.wherecamp.hackathon.phumblr.activities;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.widget.ImageView;

import com.wherecamp.hackathon.phumblr.R;
import com.wherecamp.hackathon.phumblr.fragments.FragmentAddNote;
import com.wherecamp.hackathon.phumblr.fragments.FragmentSeeWiki;
import com.wherecamp.hackathon.phumblr.models.FlickrImage;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private DotsPageIndicator mPageIndicator;
    private GridViewPager mViewPager;
    private ImageView mImageView;

    private static ArrayList<FlickrImage> flickr_images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setData();

        // Get UI references
        mImageView = (ImageView)findViewById(R.id.flickr_image);
        mImageView.setScaleType(ImageView.ScaleType.FIT_START);
        mImageView.setBackgroundColor(Color.parseColor("gray"));
        mPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        mViewPager = (GridViewPager) findViewById(R.id.pager);

        // Assigns an adapter to provide the content for this pager
        mViewPager.setAdapter(new FlickrGridPagerAdapter(this));

        mPageIndicator.setPager(mViewPager);
        mViewPager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int row, int column, float rowOffset,
                                       float columnOffset, int rowOffsetPixels,
                                       int columnOffsetPixels) {
                mPageIndicator.onPageScrolled(row, column, rowOffset, columnOffset, rowOffsetPixels, columnOffsetPixels);
            }

            @Override
            public void onPageSelected(int row, int column) {
                mImageView.setImageBitmap(flickr_images.get(row).getImage());
                mPageIndicator.onPageSelected(row, column);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mPageIndicator.onPageScrollStateChanged(state);
            }
        });

        mImageView.setImageBitmap(flickr_images.get(0).getImage());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private static final class FlickrGridPagerAdapter extends FragmentGridPagerAdapter {

        private static final int SUMMARY = 0;
        private static final int ADD_NOTES = 1;
        private static final int WIKIS = 2;
        private static final int COLUMNS = 3;

        private FlickrGridPagerAdapter(FragmentActivity activity) {
            super(activity.getFragmentManager());
        }

        @Override
        public Fragment getFragment(int row, int column) {
            switch (column) {
                case SUMMARY:
                    String title = flickr_images.get(row).getTitle();
                    String views = flickr_images.get(row).getViews();
                    return CardFragment.create(title, views);
                case ADD_NOTES:
                    return new FragmentAddNote();
                case WIKIS:
                    return new FragmentSeeWiki();
                default:
                    throw new IllegalArgumentException("getFragment(row=" + row + ", column=" + column + ")");
            }
        }

        @Override
        public int getRowCount() {
            return flickr_images.size();
        }

        @Override
        public int getColumnCount(int row) {
            return COLUMNS;
        }
    }


    private void setData() {
        Bitmap icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.bmw);
        Bitmap icon2 = BitmapFactory.decodeResource(getResources(), R.drawable.lambo);
        Bitmap icon3 = BitmapFactory.decodeResource(getResources(), R.drawable.what);
        flickr_images.add(new FlickrImage("167", "bmw", icon1));
        flickr_images.add(new FlickrImage("37", "lambo", icon2));
        flickr_images.add(new FlickrImage("17", "what", icon3));
    }
}
