package com.colortv.sample;

import android.os.Bundle;

import com.colortv.android.api.storage.ColorTvContentRecommendationConfig;
import com.colortv.android.api.storage.ColorTvContentRecommendationConfig.Device;

public class CustomRecommendationCenterActivity extends ExoPlayerVideoActivity {

    private static final String TAG = CustomRecommendationCenterActivity.class.getSimpleName();
    private ColorTvContentRecommendationConfig recommendationConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recommendationConfig = recommendationsController.getConfig();
        setRecommendationCenterConfiguration();
    }

    private void setRecommendationCenterConfiguration() {
        recommendationConfig.resetToDefault();

        recommendationConfig.setItemLayout(Device.TV, R.layout.custom_item_layout);
        recommendationConfig.setGridLayout(Device.TV, R.layout.custom_grid_layout);
        recommendationConfig.setRowCount(Device.TV, 1);

        recommendationConfig.setItemLayout(Device.MOBILE, R.layout.custom_mobile_item_layout);
        recommendationConfig.setGridLayout(Device.MOBILE, R.layout.custom_mobile_grid_layout);

        recommendationConfig.setItemLayout(Device.TABLET, R.layout.custom_item_layout);
        recommendationConfig.setGridLayout(Device.TABLET, R.layout.custom_mobile_grid_layout);
        recommendationConfig.setRowCount(Device.TABLET, 1);
    }

}
