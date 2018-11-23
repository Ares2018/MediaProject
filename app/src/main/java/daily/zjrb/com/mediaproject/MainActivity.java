package daily.zjrb.com.mediaproject;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zjrb.daily.mediaselector.MediaSelector;
import com.zjrb.daily.mediaselector.config.MediaConfig;
import com.zjrb.daily.mediaselector.entity.MediaEntity;
import com.zjrb.daily.mediaselector.ui.MediaSelectActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<MediaEntity> selectList = new ArrayList<>();
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
                        .getModel()
                        .maxSelectNum(1)
                        .isShowSelectedNum(true)
                        .canPreview(true)
                        .openCamera(true)
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
                    for(MediaEntity entity : selectList){
                        Log.e("图片-----》", entity.getPath());
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
