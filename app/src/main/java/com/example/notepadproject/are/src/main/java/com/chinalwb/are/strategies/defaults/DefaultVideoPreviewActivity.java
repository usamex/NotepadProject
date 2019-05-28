package com.example.notepadproject.are.src.main.java.com.chinalwb.are.strategies.defaults;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.VideoView;

import com.example.notepadproject.MainActivity;
import com.example.notepadproject.R;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.strategies.VideoStrategy;

import static android.app.Activity.RESULT_CANCELED;

public class DefaultVideoPreviewActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private Intent mIntent;
    private Uri mUri;
    private View mControlsView;
    private boolean mVisible;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_are__video_show);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mVideoView = findViewById(R.id.are_video_view);

        mIntent = getIntent();
        String video = (String) mIntent.getExtras().get("local");
        if(video == null){
            video = (String) mIntent.getExtras().get("server");
        }
        mUri = Uri.parse(video);

        // Set up the user interaction to manually show or hide the system UI.
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        mVideoView.setVideoURI(mUri);
        mVideoView.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        // delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
    }

}
