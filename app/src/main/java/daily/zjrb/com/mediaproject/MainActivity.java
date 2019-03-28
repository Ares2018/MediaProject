package daily.zjrb.com.mediaproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zjrb.daily.mediaselector.MediaSelector;
import com.zjrb.daily.mediaselector.config.MediaConfig;
import com.zjrb.daily.mediaselector.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import cn.zgy.picture.PictureEdit;

public class MainActivity extends AppCompatActivity {

    private List<LocalMedia> selectList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaSelector.create(MainActivity.this)
                        .type(MediaConfig.TYPE_ALL)
                        .camera(false)
                        .maxSelectNum(3)
                        .isShowSelectedNum(true)
                        .canPreview(true)
                        .openCamera(true)
                        .imageSpanCount(3)
                        .isZoomAnim(true)
                        .setOutputCameraPath("")
                        .gesture(true)
                        .forResult(MediaConfig.CHOOSE_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MediaConfig.CHOOSE_REQUEST:
                    selectList = MediaSelector.obtainSelectResult(data);
                    for(LocalMedia entity : selectList){
                        PictureEdit.create()
                                .inputFile(entity.getPath())
                                .outputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Picture"+ "/" + System.currentTimeMillis()/1000 + ".jpg")
                                .quality(30)
                                .optimize(true)
                                .ignoreSize(100)
                                .compress();
                        Log.e("图片-----》", entity.getPath());
                        if(entity.isCompressed()){
                            Log.e("压缩图片-----》", entity.getCompressPath());
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
