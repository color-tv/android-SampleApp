package com.replay.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.colortv.android.ColorTvAdListener;
import com.colortv.android.ColorTvError;
import com.colortv.android.ColorTvSdk;
import com.colortv.android.OnCurrencyEarnedListener;

public class MainActivity extends Activity {

    public static final String DISCOVERY_CENTER_PLACEMENT = "DemoAppWall";
    public static final String INTERSTITIAL_PLACEMENT = "DemoInterstitial";
    public static final String ENGAGEMENT_PLACEMENT = "DemoFullScreen";
    public static final String VIDEO_PLACEMENT = "DemoVideo";

    private Button showDiscoveryCenterButton;
    private Button showInterstitialButton;
    private Button showEngagementButton;
    private Button showVideoButton;

    private ColorTvAdListener loadAdListener = new ColorTvAdListener() {

        @Override
        public void onAdLoaded(String placement) {
            switchShowButton(placement, true);
        }

        @Override
        public void onAdError(String placement, ColorTvError error) {
            Log.e(MainActivity.class.getSimpleName(), "Error while fetching ad for placement " + placement + " - " + error.getErrorCode().name() + ": " + error.getErrorMessage());
        }

        @Override
        public void onAdClosed(String placement) {
            switchShowButton(placement, false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        ColorTvSdk.setDebugMode(true);
        ColorTvSdk.init(this, "566dbcc3326aeb750132fdfa");
        ColorTvSdk.addOnCurrencyEarnedListener(new OnCurrencyEarnedListener() {
            @Override
            public void onCurrencyEarned(int currencyAmount, String currencyType) {
                Log.d("ColorTvSdk", "Received " + currencyAmount + currencyType);
                Toast.makeText(MainActivity.this, "Received " + currencyAmount + "x" + currencyType, Toast.LENGTH_LONG).show();
            }
        });

        ColorTvSdk.registerAdListener(loadAdListener);

        ColorTvSdk.onCreate();
    }

    private void initViews() {
        showDiscoveryCenterButton = (Button) findViewById(R.id.btnShowDiscoveryCenter);
        showInterstitialButton= (Button) findViewById(R.id.btnShowInterstitial);
        showEngagementButton = (Button) findViewById(R.id.btnShowEngagement);
        showVideoButton = (Button) findViewById(R.id.btnShowVideo);

        Button btnLoadDiscoveryCenter = (Button) findViewById(R.id.btnLoadDiscoveryCenter);
        Button btnLoadInterstitial = (Button) findViewById(R.id.btnLoadInterstitial);
        Button btnLoadEngagement = (Button) findViewById(R.id.btnLoadEngagement);
        Button btnLoadVideo = (Button) findViewById(R.id.btnLoadVideo);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnShowDiscoveryCenter:
                        ColorTvSdk.showAd("DemoAppWall");
                        break;
                    case R.id.btnShowInterstitial:
                        ColorTvSdk.showAd("DemoInterstitial");
                        break;
                    case R.id.btnShowEngagement:
                        ColorTvSdk.showAd("DemoFullScreen");
                        break;
                    case R.id.btnShowVideo:
                        ColorTvSdk.showAd("DemoVideo");
                        break;
                    case R.id.btnLoadDiscoveryCenter:
                        ColorTvSdk.loadAd("DemoAppWall");
                        break;
                    case R.id.btnLoadInterstitial:
                        ColorTvSdk.loadAd("DemoInterstitial");
                        break;
                    case R.id.btnLoadEngagement:
                        ColorTvSdk.loadAd("DemoFullScreen");
                        break;
                    case R.id.btnLoadVideo:
                        ColorTvSdk.loadAd("DemoVideo");
                        break;
                }

            }
        };

        showDiscoveryCenterButton.setOnClickListener(buttonClickListener);
        showInterstitialButton.setOnClickListener(buttonClickListener);
        showEngagementButton.setOnClickListener(buttonClickListener);
        showVideoButton.setOnClickListener(buttonClickListener);
        btnLoadDiscoveryCenter.setOnClickListener(buttonClickListener);
        btnLoadInterstitial.setOnClickListener(buttonClickListener);
        btnLoadEngagement.setOnClickListener(buttonClickListener);
        btnLoadVideo.setOnClickListener(buttonClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ColorTvSdk.clearOnCurrencyEarnedListeners();
        ColorTvSdk.onDestroy();
    }

    private void switchShowButton(String placement, boolean enabled) {
        switch (placement) {
            case DISCOVERY_CENTER_PLACEMENT:
                showDiscoveryCenterButton.setEnabled(enabled);
                showDiscoveryCenterButton.setFocusable(enabled);
                break;
            case INTERSTITIAL_PLACEMENT:
                showInterstitialButton.setEnabled(enabled);
                showInterstitialButton.setFocusable(enabled);
                break;
            case ENGAGEMENT_PLACEMENT:
                showEngagementButton.setEnabled(enabled);
                showEngagementButton.setFocusable(enabled);
                break;
            case VIDEO_PLACEMENT:
                showVideoButton.setEnabled(enabled);
                showVideoButton.setFocusable(enabled);
                break;
        }
    }
}
