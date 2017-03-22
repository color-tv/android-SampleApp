package com.colortv.sample;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.colortv.android.api.ColorTvError;
import com.colortv.android.api.ColorTvSdk;
import com.colortv.android.api.ColorTvTrackingEventType;
import com.colortv.android.api.controller.ColorTvRecommendationsController;
import com.colortv.android.api.controller.ColorTvVideoTrackingController;
import com.colortv.android.api.listener.ColorTvContentRecommendationListener;

import java.util.Map;

public class VideoActivity extends Activity {

    private static final String TAG = VideoActivity.class.getSimpleName();
    private VideoView videoView;
    private String videoId;
    private String currentPlacement = "TestContent";
    private ColorTvRecommendationsController recommendationsController;
    private ColorTvVideoTrackingController videoTrackingController;

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
        public void onRecommendationCenterClosed(String placement) {
            Log.d(TAG, "Recommendation Center has closed for placement: " + placement);
        }

        @Override
        public void onUpNextClosed(String placement) {
            Log.d(TAG, "UpNext has closed for placement: " + placement);
        }

        @Override
        public void onContentChosen(String videoId, String videoUrl, Map<String, String> videoParams) {
            Intent intent = new Intent(VideoActivity.this, VideoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(MainActivity.VIDEO_URL, videoUrl);
            intent.putExtra(MainActivity.VIDEO_ID, videoId);
            startActivity(intent);
        }

        @Override
        public void onExpired(String placement) {
            Log.d(TAG, "ContentRecommendation has expired for placement: " + placement);
            recommendationsController.load(placement, videoId);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        recommendationsController = ColorTvSdk.getRecommendationsController();
        recommendationsController.registerListener(loadContentRecommendationListener);
        recommendationsController.load(currentPlacement, videoId);
        videoTrackingController = ColorTvSdk.getVideoTrackingController();

        final String videoUrl = getIntent().getExtras().getString(MainActivity.VIDEO_URL);
        videoId = getIntent().getExtras().getString(MainActivity.VIDEO_ID);
        videoView = (VideoView) findViewById(R.id.vvVideoPlay);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoTrackingController.reportVideoTrackingEvent(videoId, ColorTvTrackingEventType.VIDEO_COMPLETED, videoView.getDuration());
                recommendationsController.showRecommendationCenter(currentPlacement);
            }
        });
        videoView.setVideoURI(Uri.parse(videoUrl));
    }

    @Override
    public void onBackPressed() {
        videoTrackingController.reportVideoTrackingEvent(videoId, ColorTvTrackingEventType.VIDEO_STOPPED, videoView.getDuration());
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!videoView.isPlaying()) {
            videoTrackingController.reportVideoTrackingEvent(videoId, ColorTvTrackingEventType.VIDEO_RESUMED, videoView.getCurrentPosition());
        } else {
            videoTrackingController.reportVideoTrackingEvent(videoId, ColorTvTrackingEventType.VIDEO_STARTED, 0);
        }
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
        videoTrackingController.reportVideoTrackingEvent(videoId, ColorTvTrackingEventType.VIDEO_PAUSED, videoView.getCurrentPosition());
    }

}
