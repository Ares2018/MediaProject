package com.zjrb.daily.mediaselector.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zjrb.daily.mediaselector.MediaSelector;
import com.zjrb.daily.mediaselector.R;
import com.zjrb.daily.mediaselector.binder.CameraBinder;
import com.zjrb.daily.mediaselector.binder.MediaBinder;
import com.zjrb.daily.mediaselector.config.MediaConfig;
import com.zjrb.daily.mediaselector.dao.LocalMediaDaoHelper;
import com.zjrb.daily.mediaselector.decoration.GridSpacingItemDecoration;
import com.zjrb.daily.mediaselector.entity.MediaEntity;
import com.zjrb.daily.mediaselector.listener.OnItemClickListener;
import com.zjrb.daily.mediaselector.permissions.RxPermissions;
import com.zjrb.daily.mediaselector.ui.fragment.ImagePreviewFragment;
import com.zjrb.daily.mediaselector.util.MediaFileUtils;
import com.zjrb.daily.mediaselector.util.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.drakeet.multitype.ClassLinker;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.zjrb.daily.mediaselector.ui.fragment.ImagePreviewFragment.OnClick;
import static com.zjrb.daily.mediaselector.ui.fragment.ImagePreviewFragment.newInstance;


public class MediaSelectActivity extends MediaBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnItemClickListener, OnClick {


    TextView tvComplete;
    RecyclerView recycler;
    FrameLayout previewFrame;

    PreviewViewHolder previewHolder;


    MultiTypeAdapter adapter;
    ArrayList<MediaEntity> items = new ArrayList<>();


    /**
     * 选择的照片返回数据 key
     */
    public static final String KEY_DATA = "key_data";


    private ArrayList<MediaEntity> selectedList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_select);
        initView();
        StatusBarUtil.immersive(this);
        initPermission();
    }

    private void initView() {
        tvComplete = findViewById(R.id.tv_complete);
        previewFrame = findViewById(R.id.frame);
        findViewById(R.id.iv_back).setOnClickListener(this);
        tvComplete.setOnClickListener(this);

        recycler = findViewById(R.id.recycleview);
        recycler.setLayoutManager(new GridLayoutManager(this, config.imageSpanCount));
        recycler.addItemDecoration(new GridSpacingItemDecoration(config.imageSpanCount, 3, true));
        adapter = new MultiTypeAdapter();
//        adapter.register(MediaEntity.class, new MediaBinder(this, this));

        adapter.register(MediaEntity.class).to(
                new CameraBinder(this),
                new MediaBinder(this, this, config)
        ).withClassLinker(new ClassLinker<MediaEntity>() {
            @NonNull
            @Override
            public Class<? extends ItemViewBinder<MediaEntity, ?>> index(int position, @NonNull MediaEntity mediaEntity) {
                if (mediaEntity.isCamera()) {
                    return CameraBinder.class;
                } else {
                    return MediaBinder.class;
                }
            }
        });

        recycler.setAdapter(adapter);

    }


    private void initPermission() {

        new RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            new MediaQueryTask().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "选择照片需要访问权限",
                                    Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * 异步查询资源文件
     *
     * @author a_liYa
     * @date 16/10/21 下午10:17.
     */
    private class MediaQueryTask extends AsyncTask<Integer, Integer, List<MediaEntity>> {

        @Override
        protected List<MediaEntity> doInBackground(Integer... params) {
            return new LocalMediaDaoHelper(MediaSelectActivity.this).queryImageMedia();
        }

        @Override
        protected void onPostExecute(List<MediaEntity> results) {
            if (config.openCamera) {
                MediaEntity entity = new MediaEntity();
                entity.setCamera(true);
                items.add(entity);
            }
            items.addAll(results);
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            onBackPressed();
        } else if (view.getId() == R.id.tv_complete) {
            complete();
        }

    }

    private void complete() {
        if (selectedList != null && selectedList.size() > 0) {
            Intent data = MediaSelector.putIntentResult(selectedList);
            setResult(RESULT_OK, data);
        }
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (previewFrame.getVisibility() == View.VISIBLE) {
                previewFrame.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        if (config.openCamera && position == 0) {
            startCamera();
        } else if (config.canPreview) {
            if (previewHolder == null) {
                previewHolder = new PreviewViewHolder(items);
            }
            previewHolder.show(position);
        }
    }

    private void startCamera(){
        new RxPermissions(this).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            startOpenCamera();
                        } else {
                            Toast.makeText(getApplicationContext(), "拍照需要相机权限权限",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 启动相机
     */
    private void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            int type = MediaConfig.TYPE_IMAGE;
            File cameraFile = MediaFileUtils.createCameraFile(this,
                    type,
                    outputCameraPath, config.suffixType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                cameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, MediaConfig.REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            MediaEntity mediaEntity;
            switch (requestCode) {
                case MediaConfig.REQUEST_CAMERA:
                    final File file = new File(cameraPath);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    int degree = MediaFileUtils.readPictureDegree(file.getAbsolutePath());
                    rotateImage(degree, file);
                    mediaEntity = new MediaEntity();
                    mediaEntity.setPath(cameraPath);
                    mediaEntity.setSelected(true);
                    //添加到列表并选中
                    items.add(1, mediaEntity);
                    if(selectedList.size() < config.maxSelectNum){
                        selectedList.add(mediaEntity);
                    }else if(config.maxSelectNum == 1 && selectedList.size() == 1){
                        MediaEntity entity = selectedList.get(0);
                        int index = items.indexOf(entity);
                        if(index >= 0){
                            items.get(index).setSelected(false);
                        }
                        selectedList.remove(0);
                        selectedList.add(mediaEntity);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        Object tag = compoundButton.getTag();
        if (tag instanceof MediaEntity) {
            if (isChecked) {
                //已到最大个数
                if (selectedList.size() == config.maxSelectNum && 1 != config.maxSelectNum) {
                    compoundButton.setOnCheckedChangeListener(null);
                    compoundButton.setChecked(false);
                    compoundButton.setOnCheckedChangeListener(this);
                    ((MediaEntity) tag).setSelected(false);
                    Toast.makeText(this, "你最多只能选择" + config.maxSelectNum + "张照片", Toast.LENGTH_SHORT).show();
                    return;
                    //单选
                } else if (selectedList.size() == config.maxSelectNum && 1 == config.maxSelectNum) {
                    clearSelected();
                }
                selectedList.add((MediaEntity) tag);
                updateItems((MediaEntity) tag, isChecked);
            } else {
                selectedList.remove(tag);
                updateItems((MediaEntity) tag, isChecked);
            }
        }

        if (selectedList == null || selectedList.isEmpty() || !config.isShowSelectedNum) {
            tvComplete.setText("完成");
        } else {
            tvComplete.setText(selectedList.size() + " 完成");
        }

    }

    /**
     * 更新item选中状态
     *
     * @param entity
     * @param isSelected
     */
    private void updateItems(MediaEntity entity, boolean isSelected) {
        int index = items.indexOf(entity);
        if (index >= 0) {
            items.get(index).setSelected(isSelected);
        }
    }

    private void clearSelected() {
        if (selectedList != null) {
            for (MediaEntity entity : selectedList) {
                int indexOf = items.indexOf(entity);
                if (indexOf >= 0) {
                    items.get(indexOf).setSelected(false);
                    adapter.notifyItemChanged(indexOf);
                }
            }
            selectedList.clear();
        }
    }

    @Override
    public void onPreviewClick(View view) {
        previewFrame.setVisibility(View.GONE);
    }


    private class PreviewViewHolder {

        ViewPager viewPager;

        PagerAdapter pagerAdapter;

        List<MediaEntity> list;

        public PreviewViewHolder(List<MediaEntity> list) {
            this.list = list;
            viewPager = (ViewPager) previewFrame.findViewById(R.id.view_pager);
        }

        public void show(int index) {
            if (pagerAdapter == null) {
                pagerAdapter = new ImagePreviewAdapter(getSupportFragmentManager(),
                        getStringList(list));
                viewPager.setAdapter(pagerAdapter);
            }

            viewPager.setCurrentItem(index, false);
            previewFrame.setVisibility(View.VISIBLE);
        }


        /**
         * 获取图片List列表
         *
         * @param list 数据源
         * @return list集合
         */
        public List<String> getStringList(List<MediaEntity> list) {
            List<String> resultList = new ArrayList<>(list.size());
            for (MediaEntity data : list) {
                resultList.add(data.getPath());
            }
            return resultList;
        }

    }

    /**
     * 图片预览 ViewPager 适配器
     *
     * @author a_liYa
     * @date 16/10/25 12:30.
     */
    private class ImagePreviewAdapter extends FragmentStatePagerAdapter {

        private List<String> data;

        public ImagePreviewAdapter(FragmentManager fm, List<String> list) {
            super(fm);
            this.data = list;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Fragment getItem(int position) {
            // TODO: 2018/9/13
            ImagePreviewFragment imagePreviewFragment = newInstance(data.get(position));
            return imagePreviewFragment;
        }

    }
}
