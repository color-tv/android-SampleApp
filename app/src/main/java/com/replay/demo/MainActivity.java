package com.replay.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.replay.android.AdPlacement;
import com.replay.android.AdType;
import com.replay.android.OnCurrencyEarnedListener;
import com.replay.android.ReplaySdk;
import com.replay.android.dagger.DaggerReplaySdkComponent;
import com.replay.android.dagger.ReplaySdkModule;

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
                ReplaySdk.showAd("TestAppWall");
            }
        });

        findViewById(R.id.btnInterstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaySdk.showAd("TestInterstitial");
            }
        });

        findViewById(R.id.btnFullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaySdk.showAd("TestFullScreen");
            }
        });

        DaggerTestComponent.builder()
                .testModule(new TestModule(this))
                .build();

//        new TestComponent().inject();
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
