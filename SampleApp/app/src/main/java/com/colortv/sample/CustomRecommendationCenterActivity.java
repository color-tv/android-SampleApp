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

        recommendationConfig.setItemLayout(Device.TV, R.layout.custom_yt_item_layout);
        recommendationConfig.setGridLayout(Device.TV, R.layout.custom_mobile_grid_layout);
        recommendationConfig.setRowCount(Device.TV, 3);

        recommendationConfig.setItemLayout(Device.MOBILE, R.layout.custom_mobile_yt_item_layout);
        recommendationConfig.setGridLayout(Device.MOBILE, R.layout.custom_mobile_grid_layout);
        recommendationConfig.setSnapEnabled(false);

        recommendationConfig.setItemLayout(Device.TABLET, R.layout.custom_mobile_yt_item_layout);
        recommendationConfig.setGridLayout(Device.TABLET, R.layout.custom_mobile_grid_layout);
        recommendationConfig.setRowCount(Device.TABLET, 1);

        /*
        --------------------------------------------------------------------------------------------
        OTHER COMBINATIONS

        recommendationConfig.setItemLayout(Device.TV, R.layout.custom_item_layout); // or custom_item_layout_2
        recommendationConfig.setGridLayout(Device.TV, R.layout.custom_grid_layout);
        recommendationConfig.setRowCount(Device.TV, 1);

        recommendationConfig.setItemLayout(Device.MOBILE, R.layout.custom_mobile_item_layout);
        recommendationConfig.setGridLayout(Device.MOBILE, R.layout.custom_mobile_grid_layout);
        recommendationConfig.setSnapEnabled(true);

        recommendationConfig.setItemLayout(Device.TABLET, R.layout.custom_mobile_item_layout);
        recommendationConfig.setGridLayout(Device.TABLET, R.layout.custom_mobile_grid_layout);
        recommendationConfig.setRowCount(Device.TABLET, 2);

        --------------------------------------------------------------------------------------------
        DEFAULT

        recommendationConfig.setItemLayout(Device.TV, R.layout.colortv_default_item_layout); // or colortv_default_simplified_item_layout
        recommendationConfig.setGridLayout(Device.TV, R.layout.colortv_default_grid_layout);
        recommendationConfig.setRowCount(Device.TV, 2);

        recommendationConfig.setItemLayout(Device.MOBILE, R.layout.colortv_default_mobile_item_layout);
        recommendationConfig.setGridLayout(Device.MOBILE, R.layout.colortv_default_mobile_grid_layout);
        recommendationConfig.setSnapEnabled(true);

        recommendationConfig.setItemLayout(Device.TABLET, R.layout.colortv_default_mobile_item_layout);
        recommendationConfig.setGridLayout(Device.TABLET, R.layout.colortv_default_mobile_grid_layout);
        recommendationConfig.setRowCount(Device.TABLET, 2);
        */
    }

}
