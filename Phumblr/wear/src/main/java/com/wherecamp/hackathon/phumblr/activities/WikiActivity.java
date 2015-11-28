package com.wherecamp.hackathon.phumblr.activities;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
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
import com.wherecamp.hackathon.phumblr.models.Wikipedia;
import com.wherecamp.hackathon.phumblr.services.WearApplication;

import java.util.ArrayList;

public class WikiActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private DotsPageIndicator mPageIndicator;
    private GridViewPager mViewPager;
    private ImageView mImageView;

    private static ArrayList<Wikipedia> wikis = new ArrayList<>();
    private static ArrayList<FlickrImage> flickr_images = new ArrayList<>();

    private int image_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);

        wikis = WearApplication.getWikis();
        flickr_images = WearApplication.getFlickrImages();

        image_id = getIntent().getIntExtra("image_id", 0);

        // Get UI references
        mImageView = (ImageView) findViewById(R.id.wiki_image);
        mImageView.setScaleType(ImageView.ScaleType.FIT_START);
        mImageView.setBackgroundColor(Color.parseColor("gray"));
        mPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        mViewPager = (GridViewPager) findViewById(R.id.pager);

        // Assigns an adapter to provide the content for this pager
        mViewPager.setAdapter(new WikiGridPagerAdapter(this));

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
                mImageView.setImageBitmap(flickr_images.get(image_id).getImage());
                mPageIndicator.onPageSelected(row, column);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mPageIndicator.onPageScrollStateChanged(state);
            }
        });

        mImageView.setImageBitmap(flickr_images.get(image_id).getImage());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private static final class WikiGridPagerAdapter extends FragmentGridPagerAdapter {

        private static final int SECTION1 = 0;

        private WikiGridPagerAdapter(FragmentActivity activity) {
            super(activity.getFragmentManager());
        }

        @Override
        public Fragment getFragment(int row, int column) {
            ArrayList<String[]> sections = wikis.get(row).getSections();

            switch (column) {
                case SECTION1:
                    String title1 = wikis.get(row).getTitle();
                    String views1 = "Distance to entry is "+wikis.get(row).getDistance()+" m";
                    return CardFragment.create(title1, views1);

                default:
                    String title2 = sections.get(column-1)[0];
                    String views2 = sections.get(column-1)[1];
                    return CardFragment.create(title2, views2);

            }
        }

        @Override
        public int getRowCount() {
            return wikis.size();
        }

        @Override
        public int getColumnCount(int row) {
            int max = 0;
            for (Wikipedia wiki :wikis) {
                int len = wiki.getSections().size();
                if (len > max) {
                    max = len;
                }
            }
            return 5;
        }
    }
}
