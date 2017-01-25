package com.colortv.demo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.colortv.android.ColorTvContentRecommendationListener;
import com.colortv.android.ColorTvError;
import com.colortv.android.ColorTvSdk;
import com.colortv.android.TrackingEventType;

public class VideoActivity extends Activity {

    private static final String TAG = VideoActivity.class.getSimpleName();
    private VideoView videoView;
    private String videoId;
    private String currentPlacement = "TestContent";

    private ColorTvContentRecommendationListener loadContentRecommendationListener = new ColorTvContentRecommendationListener() {

        @Override
        public void onLoaded(String placement) {
            currentPlacement = placement;
        }

        @Override
        public void onError(String placement, ColorTvError error) {
            Log.e(TAG, "Error while fetching ContentRecommendation for placement " +
                    placement + " - " + error.getErrorCode().name() + ": " + error.getErrorMessage());
        }

        @Override
        public void onClosed(String placement) {
            Log.d(TAG, "ContentRecommendation has closed for placement: " + placement);
        }

        @Override
        public void onExpired(String placement) {
            Log.d(TAG, "ContentRecommendation has expired for placement: " + placement);
            ColorTvSdk.loadContentRecommendation(placement, videoId);
        }

        @Override
        public void onContentChosen(String videoId, String videoUrl) {
            super.onContentChosen(videoId, videoUrl);
            Intent intent = new Intent(VideoActivity.this, VideoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(MainActivity.VIDEO_URL, videoUrl);
            intent.putExtra(MainActivity.VIDEO_ID, videoId);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final String videoUrl = getIntent().getExtras().getString(MainActivity.VIDEO_URL);
        videoId = getIntent().getExtras().getString(MainActivity.VIDEO_ID);
        videoView = (VideoView) findViewById(R.id.vvVideoPlay);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ColorTvSdk.reportVideoTrackingEvent(videoId, TrackingEventType.VIDEO_COMPLETED, videoView.getDuration());
                ColorTvSdk.showContentRecommendation(currentPlacement);
            }
        });
        videoView.setVideoURI(Uri.parse(videoUrl));
        ColorTvSdk.registerContentRecommendationListener(loadContentRecommendationListener);
        ColorTvSdk.loadContentRecommendation(currentPlacement, videoId);
    }

    @Override
    public void onBackPressed() {
        ColorTvSdk.reportVideoTrackingEvent(videoId, TrackingEventType.VIDEO_STOPPED, videoView.getDuration());
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!videoView.isPlaying()) {
            ColorTvSdk.reportVideoTrackingEvent(videoId, TrackingEventType.VIDEO_RESUMED, videoView.getCurrentPosition());
        } else {
            ColorTvSdk.reportVideoTrackingEvent(videoId, TrackingEventType.VIDEO_STARTED, 0);
        }
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
        ColorTvSdk.reportVideoTrackingEvent(videoId, TrackingEventType.VIDEO_PAUSED, videoView.getCurrentPosition());
    }

}
