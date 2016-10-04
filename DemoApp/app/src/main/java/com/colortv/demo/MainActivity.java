package com.colortv.demo;

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
import com.colortv.android.Placements;

public class MainActivity extends Activity {


    private ColorTvAdListener loadAdListener = new ColorTvAdListener() {

        @Override
        public void onAdLoaded(String placement) {
            ColorTvSdk.showAd(placement);
        }

        @Override
        public void onAdError(String placement, ColorTvError error) {
            Log.e(MainActivity.class.getSimpleName(), "Error while fetching ad for placement " + placement + " - " + error.getErrorCode().name() + ": " + error.getErrorMessage());
        }

        @Override
        public void onAdClosed(String placement, boolean watched) {
            Log.d(MainActivity.class.getSimpleName(), "Ad has closed for placement: " + placement + " and watched: " + watched);
        }

        @Override
        public void onAdExpired(String placement) {
            Log.d(MainActivity.class.getSimpleName(), "Ad has expired for placement: " + placement);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        ColorTvSdk.setDebugMode(true);
        ColorTvSdk.init(this, "08271056-5211-4ae6-bf1c-12e344455383");
        ColorTvSdk.addOnCurrencyEarnedListener(new OnCurrencyEarnedListener() {
            @Override
            public void onCurrencyEarned(String placement, int currencyAmount, String currencyType) {
                Log.d("ColorTvSdk", "Received " + currencyAmount + " x " + currencyType);
                Toast.makeText(MainActivity.this, "Received " + currencyAmount + " x " + currencyType, Toast.LENGTH_LONG).show();
            }
        });

        ColorTvSdk.registerAdListener(loadAdListener);

        ColorTvSdk.onCreate();
    }

    private void initViews() {
        Button showRandomAdButton = (Button) findViewById(R.id.btnShowRandom);
        Button showDiscoveryCenterButton = (Button) findViewById(R.id.btnShowDiscoveryCenter);
        Button showAppFeatureButton= (Button) findViewById(R.id.btnShowAppFeature);
        Button showDirectEngagementButton = (Button) findViewById(R.id.btnShowDirectEngagement);
        Button showVideoButton = (Button) findViewById(R.id.btnShowVideo);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnShowRandom:
                        ColorTvSdk.loadAd(Placements.MAIN_MENU);
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

        showRandomAdButton.setOnClickListener(buttonClickListener);
        showDiscoveryCenterButton.setOnClickListener(buttonClickListener);
        showAppFeatureButton.setOnClickListener(buttonClickListener);
        showDirectEngagementButton.setOnClickListener(buttonClickListener);
        showVideoButton.setOnClickListener(buttonClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ColorTvSdk.clearOnCurrencyEarnedListeners();
        ColorTvSdk.onDestroy();
    }
}
