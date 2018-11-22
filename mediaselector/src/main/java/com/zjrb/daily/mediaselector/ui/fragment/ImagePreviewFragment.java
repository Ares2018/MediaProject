package com.zjrb.daily.mediaselector.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zjrb.daily.mediaselector.R;


public class ImagePreviewFragment extends Fragment implements View.OnClickListener{

    OnClick click;
    private static final String ARGS = "args";
    //图片url
    private String mUrl;

    private ImageView preview;

    public static ImagePreviewFragment newInstance(String url) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();

        Bundle args = new Bundle();
        args.putString(ARGS, url);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnClick(OnClick click)
    {
        this.click = click;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARGS);

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
        preview.setOnClickListener(this);
        loadImage();
    }

    private void loadImage() {
        Glide.with(getContext()).load(mUrl).into(preview);
    }

    @Override
    public void onClick(View view) {
        if(click != null){
            click.onPreviewClick(view);
        }
    }

    public interface OnClick{
        void onPreviewClick(View view);
    }
}
