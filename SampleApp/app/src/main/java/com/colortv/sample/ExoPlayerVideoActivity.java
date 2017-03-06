package com.colortv.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.colortv.android.api.ColorTvError;
import com.colortv.android.api.ColorTvSdk;
import com.colortv.android.api.controller.ColorTvRecommendationsController;
import com.colortv.android.api.controller.ColorTvVideoTrackingController;
import com.colortv.android.api.listener.ColorTvContentRecommendationListener;
import com.colortv.android.api.storage.ColorTvContentRecommendationConfig.Device;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Map;

public class ExoPlayerVideoActivity extends Activity {

    private static final String TAG = ExoPlayerVideoActivity.class.getSimpleName();
    private SimpleExoPlayer simpleExoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;
    private String videoId;
    private String currentPlacement = "TestContent";
    private ColorTvRecommendationsController recommendationsController;
    private ColorTvVideoTrackingController videoTrackingController;

    private ColorTvContentRecommendationListener loadContentRecommendationListener = new ColorTvContentRecommendationListener() {

        @Override
        public void onLoaded(String placement) {
            currentPlacement = placement;
            recommendationsController.showRecommendationCenter(currentPlacement);
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
        public void onContentChosen(String videoId, String videoUrl, Map<String, String> videoParams) {
            Intent intent = new Intent(ExoPlayerVideoActivity.this, ExoPlayerVideoActivity.class);
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

    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                recommendationsController.showRecommendationCenter(currentPlacement);
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo);

        recommendationsController = ColorTvSdk.getRecommendationsController();
        recommendationsController.registerListener(loadContentRecommendationListener);
        recommendationsController.load(currentPlacement, videoId);
        recommendationsController.getConfig().setItemLayout(Device.TV, R.layout.custom_item_layout);
        videoTrackingController = ColorTvSdk.getVideoTrackingController();

        final String videoUrl = getIntent().getExtras().getString(MainActivity.VIDEO_URL);
        videoId = getIntent().getExtras().getString(MainActivity.VIDEO_ID);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoVideo);

        initExoPlayer();
        loadVideo(videoUrl);
    }

    private void loadVideo(String videoUrl) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "DemoApp"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(videoUrl),
                dataSourceFactory, extractorsFactory, null, null);
        simpleExoPlayer.prepare(videoSource);
    }

    private void initExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayer.addListener(eventListener);
        simpleExoPlayerView.setPlayer(simpleExoPlayer);
        simpleExoPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayerView.showController();
            }
        });
        videoTrackingController.setVideoIdForPlayerTracking(videoId);
        videoTrackingController.setExoPlayerToTrackVideo(simpleExoPlayer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }
}
