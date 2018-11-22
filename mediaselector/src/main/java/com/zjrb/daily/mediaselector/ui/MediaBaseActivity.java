package com.zjrb.daily.mediaselector.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.zjrb.daily.mediaselector.MediaSelectionConfig;


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
