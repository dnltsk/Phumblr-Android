package com.wherecamp.hackathon.phumblr.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wherecamp.hackathon.phumblr.R;
import com.wherecamp.hackathon.phumblr.activities.WikiActivity;

/**
 * Created by Nice Fontaine on 27.11.2015.
 */
public class FragmentSeeWiki extends Fragment {

    // Timeout delay for confirmation
    private static final long DELAY_TIMEOUT = 2500L;

    private DelayedConfirmationView mConfirmationView;
    private TextView mTextView;
    private Activity activity;

    public int getRow() {
        return row;
    }

    private int row;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate and customize the UI from its XML layout definition
        final View view = inflater.inflate(R.layout.fragment_view, container, false);
        mConfirmationView = (DelayedConfirmationView) view.findViewById(R.id.delayed_confirm);
        mConfirmationView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_read));
        mTextView = (TextView) view.findViewById(R.id.label);
        mTextView.setText("Wiki Entries Nearby");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle click in order to start the free-form recognition
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, WikiActivity.class);
                intent.putExtra("image_id", getRow());
                startActivity(intent);
            }
        });
    }

    public void setImageId(int row) {
        this.row = row;
    }
}
