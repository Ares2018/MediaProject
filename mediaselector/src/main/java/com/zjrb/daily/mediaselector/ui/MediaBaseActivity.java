package com.zjrb.daily.mediaselector.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zjrb.daily.mediaselector.config.MediaSelectionConfig;


public abstract class MediaBaseActivity extends AppCompatActivity {

    public static final String EXTRA_CONFIG = "MediaSelectionConfig";
    protected MediaSelectionConfig config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            config = savedInstanceState.getParcelable(EXTRA_CONFIG);
        } else {
            config = MediaSelectionConfig.getInstance();
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_CONFIG, config);
    }
}
