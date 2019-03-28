package com.zjrb.daily.mediaselector.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.zjrb.daily.mediaselector.MediaSelector;
import com.zjrb.daily.mediaselector.R;
import com.zjrb.daily.mediaselector.config.MediaMimeType;
import com.zjrb.daily.mediaselector.config.MediaSelectionConfig;
import com.zjrb.daily.mediaselector.entity.MediaEntity;
import com.zjrb.daily.mediaselector.ui.dialog.MediaDialog;
import com.zjrb.daily.mediaselector.util.MediaFileUtils;

import java.io.File;
import java.util.List;


public abstract class MediaBaseActivity extends AppCompatActivity {

    public static final String EXTRA_CONFIG = "MediaSelectionConfig";
    protected Context mContext;
    protected MediaSelectionConfig config;

    protected String cameraPath, outputCameraPath;

    protected MediaDialog compressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            config = savedInstanceState.getParcelable(EXTRA_CONFIG);
        } else {
            config = MediaSelectionConfig.getInstance();
        }
        super.onCreate(savedInstanceState);
        mContext = this;
        initConfig();

    }

    private void initConfig() {
        outputCameraPath = config.outputCameraPath;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_CONFIG, config);
    }


    /**
     * 生成uri
     *
     * @param cameraFile
     * @return
     */
    protected Uri parUri(File cameraFile) {
        Uri imageUri;
        String authority = getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(mContext, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }

    /**
     * 判断拍照 图片是否旋转
     *
     * @param degree
     * @param file
     */
    protected void rotateImage(int degree, File file) {
        if (degree > 0) {
            // 针对相片有旋转问题的处理方式
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
                opts.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                Bitmap bmp = MediaFileUtils.rotaingImageView(degree, bitmap);
                MediaFileUtils.saveBitmapFile(bmp, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 重新构造已压缩的图片返回集合
     *
     * @param images
     * @param files
     */
    private void handleCompressCallBack(List<MediaEntity> images, List<File> files) {
        if (files.size() == images.size()) {
            for (int i = 0, j = images.size(); i < j; i++) {
                // 压缩成功后的地址
                String path = files.get(i).getPath();
                MediaEntity image = images.get(i);
                // 如果是网络图片则不压缩
                boolean http = MediaMimeType.isHttp(path);
                boolean eqTrue = !TextUtils.isEmpty(path) && http;
                image.setCompressed(eqTrue ? false : true);
                image.setCompressPath(eqTrue ? "" : path);
            }
        }
        onResult(images);
    }

    protected void showCompressDialog() {
        if (!isFinishing()) {
            dismissCompressDialog();
            compressDialog = new MediaDialog(this);
            compressDialog.show();
        }
    }

    protected void dismissCompressDialog() {
        try {
            if (!isFinishing()
                    && compressDialog != null
                    && compressDialog.isShowing()) {
                compressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onResult(List<MediaEntity> images) {
        dismissCompressDialog();
        if (images != null && images.size() > 0) {
            Intent data = MediaSelector.putIntentResult(images);
            setResult(RESULT_OK, data);
        }
        closeActivity();
    }


    protected void closeActivity() {
        finish();
        if (config.camera) {
            overridePendingTransition(0, R.anim.fade_out);
        }
    }
}
