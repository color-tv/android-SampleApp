package com.replay.demo;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.replay.android.AdPlacement;
import com.replay.android.AdType;
import com.replay.android.OnCurrencyEarnedListener;
import com.replay.android.ReplaySdk;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReplaySdk.setDebugMode(true);
        ReplaySdk.init(this, "566dbcc3326aeb750132fdfa");
        ReplaySdk.addOnCurrencyEarnedListener(new OnCurrencyEarnedListener() {
            @Override
            public void onCurrencyEarned(int currencyAmount, String currencyType) {
                Log.d("ReplaySdk", "Received " + currencyAmount + currencyType);
            }
        });

        findViewById(R.id.btnDiscoveryCenter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaySdk.showAd(AdType.APP_WALL, AdPlacement.BETWEEN_LEVELS);
            }
        });

        findViewById(R.id.btnInterstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaySdk.showAd(AdType.INTERSTITIAL, AdPlacement.APP_RESUME);
            }
        });

        findViewById(R.id.btnFullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaySdk.showAd(AdType.FULLSCREEN, AdPlacement.LEVEL_UP);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReplaySdk.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ReplaySdk.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ReplaySdk.clearOnCurrencyEarnedListeners();
    }
}
