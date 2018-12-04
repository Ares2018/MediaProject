package com.zjrb.daily.mediaselector.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.zjrb.daily.mediaselector.R;
import com.zjrb.daily.mediaselector.ui.widget.photoview.PhotoView;


public class ImagePreviewFragment extends Fragment{

    private static final String ARGS = "args";
    //图片url
    private String mUrl;

    private boolean gesture;

    private PhotoView preview;

    public static ImagePreviewFragment newInstance(String url, boolean gesture) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();

        Bundle args = new Bundle();
        args.putString(ARGS, url);
        args.putBoolean("gesture", gesture);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARGS);
            gesture = getArguments().getBoolean("gesture");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preview = view.findViewById(R.id.preview);
        if(gesture) {
            preview.enable();
            preview.enableRotate();
        }
        loadImage();
    }

    private void loadImage() {
        Glide.with(preview.getContext()).load(mUrl).into(preview);

    }
}
