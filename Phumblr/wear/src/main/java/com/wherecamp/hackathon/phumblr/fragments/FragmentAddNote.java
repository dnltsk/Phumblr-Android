package com.wherecamp.hackathon.phumblr.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wherecamp.hackathon.phumblr.R;

import java.util.List;

/**
 * Created by Nice Fontaine on 27.11.2015.
 */
public final class FragmentAddNote
        extends Fragment
        implements DelayedConfirmationView.DelayedConfirmationListener {

    // Request code to look for when retrieving free-form speech
    private static final int ADD_NOTE_REQUEST_CODE = 1;

    // Timeout delay for confirmation
    private static final long DELAY_TIMEOUT = 2500L;

    private DelayedConfirmationView mConfirmationView;
    private TextView mTextView;
    private boolean mIsAnimating = false;

    // Used to store result from free-form speech
    private String mNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate and customize the UI from its XML layout definition
        final View view = inflater.inflate(R.layout.fragment_view, container, false);
        mConfirmationView = (DelayedConfirmationView) view.findViewById(R.id.delayed_confirm);
        mConfirmationView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_edit));
        mTextView = (TextView) view.findViewById(R.id.label);
        mTextView.setText("Add Notes");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle click in order to start the free-form recognition
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsAnimating) {
                    mConfirmationView.setImageResource(R.drawable.ic_action_edit);
                    mIsAnimating = false;
                    return;
                }
                // Fire an intent to start the speech recognition activity.
                final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your notes");
                startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
            }
        });
        mConfirmationView.setTotalTimeMs(DELAY_TIMEOUT);
        mConfirmationView.setListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Retrieve our free-form speech
        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mNotes = results.get(0);
            mIsAnimating = true;
            mConfirmationView.setImageResource(R.drawable.ic_full_cancel);
            mConfirmationView.start();
        }
    }

    @Override
    public void onTimerFinished(View view) {
        final Activity activity = getActivity();

        if (activity == null) {
            // Fragment no longer belongs to the activity
            return;
        }

        mConfirmationView.reset();
        mConfirmationView.setImageResource(R.drawable.ic_action_edit);
        Toast.makeText(getActivity(), mNotes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerSelected(View view) {
        mConfirmationView.reset();
    }
}
