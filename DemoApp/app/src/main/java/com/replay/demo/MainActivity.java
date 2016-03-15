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
        public void onAdClosed(String placement) {
            Log.d(MainActivity.class.getSimpleName(), "Ad has closed for placement: " + placement);
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
                        ColorTvSdk.loadAd("MainMenu");
                        break;
                    //The placements below were configured in the UI to only show specific ad types
                    case R.id.btnShowDiscoveryCenter:
                        ColorTvSdk.loadAd("Pause");
                        break;
                    case R.id.btnShowAppFeature:
                        ColorTvSdk.loadAd("InAppPurchase");
                        break;
                    case R.id.btnShowDirectEngagement:
                        ColorTvSdk.loadAd("StageOpen");
                        break;
                    case R.id.btnShowVideo:
                        ColorTvSdk.loadAd("BetweenLevels");
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
