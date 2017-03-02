package com.colortv.demo;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.colortv.android.ColorTvAdListener;
import com.colortv.android.ColorTvError;
import com.colortv.android.ColorTvSdk;
import com.colortv.android.OnCurrencyEarnedListener;
import com.colortv.android.Placements;

public class MainActivity extends Activity {

    public static final String VIDEO_URL = "videoUrl";
    public static final String VIDEO_ID = "videoId";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String videoUrl = "https://s3.amazonaws.com/colortv-testapp-data/0001.mp4";
    private static final String videoId = "0001";

    private ColorTvAdListener loadAdListener = new ColorTvAdListener() {

        @Override
        public void onAdLoaded(String placement) {
            ColorTvSdk.showAd(placement);
        }

        @Override
        public void onAdError(String placement, ColorTvError error) {
            Log.e(TAG, "Error while fetching ad for placement " + placement + " - " + error.getErrorCode().name() + ": " + error.getErrorMessage());
        }

        @Override
        public void onAdClosed(String placement, boolean watched) {
            Log.d(TAG, "Ad has closed for placement: " + placement + " and watched: " + watched);
        }

        @Override
        public void onAdExpired(String placement) {
            Log.d(TAG, "Ad has expired for placement: " + placement);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        ColorTvSdk.setDebugMode(true);
        ColorTvSdk.init(this, "08271056-5211-4ae6-bf1c-12e344455383");
        ColorTvSdk.addOnCurrencyEarnedListener(new OnCurrencyEarnedListener() {
            @Override
            public void onCurrencyEarned(String placement, int currencyAmount, String currencyType) {
                Toast.makeText(MainActivity.this, "Received " + currencyAmount + " x " + currencyType, Toast.LENGTH_LONG).show();
            }
        });
        ColorTvSdk.registerAdListener(loadAdListener);
        ColorTvSdk.onCreate();
    }

    private void initViews() {
        Button showDiscoveryCenterButton = (Button) findViewById(R.id.btnShowDiscoveryCenter);
        Button showAppFeatureButton = (Button) findViewById(R.id.btnShowAppFeature);
        Button showDirectEngagementButton = (Button) findViewById(R.id.btnShowDirectEngagement);
        Button showVideoButton = (Button) findViewById(R.id.btnShowVideo);
        Button showContentRecommendationButton = (Button) findViewById(R.id.btnShowContentRecommendation);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnShowContentRecommendation:
                        /**
                         * If you want to use Google ExoPlayer change
                         * VideoActivity.class to ExoPlayerVideoActivity.class
                         * Both classes are examples of usage (including reporting events)
                         */
                        Intent intent = new Intent(MainActivity.this, ExoPlayerVideoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(VIDEO_URL, videoUrl);
                        intent.putExtra(VIDEO_ID, videoId);
                        startActivity(intent);
                        break;
                    //The placements below were configured in the dashboard to only show specific ad types
                    case R.id.btnShowDiscoveryCenter:
                        ColorTvSdk.loadAd(Placements.PAUSE);
                        break;
                    case R.id.btnShowAppFeature:
                        ColorTvSdk.loadAd(Placements.IN_APP_PURCHASE);
                        break;
                    case R.id.btnShowDirectEngagement:
                        ColorTvSdk.loadAd(Placements.STAGE_OPEN);
                        break;
                    case R.id.btnShowVideo:
                        ColorTvSdk.loadAd(Placements.BETWEEN_LEVELS);
                        break;
                }

            }
        };

        if (isTvDevice()) {
            showDiscoveryCenterButton.setOnClickListener(buttonClickListener);
            showAppFeatureButton.setOnClickListener(buttonClickListener);
            showDirectEngagementButton.setOnClickListener(buttonClickListener);
            showVideoButton.setOnClickListener(buttonClickListener);
        }
        showContentRecommendationButton.setOnClickListener(buttonClickListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ColorTvSdk.clearOnCurrencyEarnedListeners();
        ColorTvSdk.onDestroy();
    }

    public boolean isTvDevice() {
        return ((UiModeManager) getSystemService(UI_MODE_SERVICE)).getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }
}
