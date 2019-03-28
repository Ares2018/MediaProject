package com.zjrb.daily.mediaselector.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
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
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjrb.daily.mediaselector.R;
import com.zjrb.daily.mediaselector.binder.CameraBinder;
import com.zjrb.daily.mediaselector.binder.MediaBinder;
import com.zjrb.daily.mediaselector.config.MediaConfig;
import com.zjrb.daily.mediaselector.config.MediaMimeType;
import com.zjrb.daily.mediaselector.dao.LocalMediaLoader;
import com.zjrb.daily.mediaselector.decoration.GridSpacingItemDecoration;
import com.zjrb.daily.mediaselector.entity.LocalMedia;
import com.zjrb.daily.mediaselector.entity.LocalMediaFolder;
import com.zjrb.daily.mediaselector.listener.OnItemClickListener;
import com.zjrb.daily.mediaselector.permissions.RxPermissions;
import com.zjrb.daily.mediaselector.ui.adapter.PictureAlbumDirectoryAdapter;
import com.zjrb.daily.mediaselector.ui.fragment.ImagePreviewFragment;
import com.zjrb.daily.mediaselector.ui.widget.FolderPopWindow;
import com.zjrb.daily.mediaselector.util.MediaFileUtils;
import com.zjrb.daily.mediaselector.util.StatusBarUtil;
import com.zjrb.daily.mediaselector.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.drakeet.multitype.ClassLinker;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.zjrb.daily.mediaselector.ui.fragment.ImagePreviewFragment.newInstance;


public class MediaSelectActivity extends MediaBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnItemClickListener,PictureAlbumDirectoryAdapter.OnItemClickListener {


    FrameLayout topBar;
    TextView tvTitle;
    TextView tvComplete;
    RecyclerView recycler;
    FrameLayout previewFrame;

    PreviewViewHolder previewHolder;


    MultiTypeAdapter adapter;
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    List<LocalMedia> items = new ArrayList<>();

    private ArrayList<LocalMedia> selectedList = new ArrayList<>();

    private LocalMediaLoader mediaLoader;
    private FolderPopWindow folderWindow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (config.camera) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    , WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.media_empty);
        } else {
            setContentView(R.layout.activity_media_select);
            initView();
            StatusBarUtil.immersive(this);
        }
        initPermission();
    }

    private void initView() {
        mediaLoader = new LocalMediaLoader(this, config.mimeType, config.isGif, 0, 0);
        topBar = findViewById(R.id.top_bar);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setOnClickListener(this);
        String title = config.mimeType == MediaMimeType.ofAudio() ?
                getString(R.string.picture_all_audio)
                : getString(R.string.picture_camera_roll);
        tvTitle.setText(title);
        folderWindow = new FolderPopWindow(this, config.mimeType);
        folderWindow.setPictureTitleView(tvTitle);
        folderWindow.setOnItemClickListener(this);
        tvComplete = findViewById(R.id.tv_complete);
        previewFrame = findViewById(R.id.frame);
        findViewById(R.id.iv_back).setOnClickListener(this);
        tvComplete.setOnClickListener(this);

        recycler = findViewById(R.id.recycleview);
        recycler.setLayoutManager(new GridLayoutManager(this, config.imageSpanCount));
        recycler.addItemDecoration(new GridSpacingItemDecoration(config.imageSpanCount, 3, true));
        adapter = new MultiTypeAdapter();

        adapter.register(LocalMedia.class).to(
                new CameraBinder(this),
                new MediaBinder(this, this, config)
        ).withClassLinker(new ClassLinker<LocalMedia>() {
            @NonNull
            @Override
            public Class<? extends ItemViewBinder<LocalMedia, ?>> index(int position, @NonNull LocalMedia mediaEntity) {
                if (mediaEntity.isCamera()) {
                    return CameraBinder.class;
                } else {
                    return MediaBinder.class;
                }
            }
        });

        recycler.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int a = 0;
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
                            if (config.camera) {
                                startCamera();
                            } else {
//                                new MediaQueryTask().execute();
                                readLocalMedia();
                            }
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
     * 读取media
     *
     * @author zhengy
     * create at 2019/3/28 下午2:46
     **/
    protected void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    foldersList = folders;
                    LocalMediaFolder folder = folders.get(0);
                    folder.setChecked(true);
                    folderWindow.bindFolder(folders);
                    List<LocalMedia> localImg = folder.getImages();
                    initData(localImg);
                    adapter.setItems(items);
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    private void initData(List<LocalMedia> images){
        items.clear();
        if (config.openCamera) {
            LocalMedia entity = new LocalMedia();
            entity.setCamera(true);
            items.add(entity);
        }
        items.addAll(images);
    }
//    private class MediaQueryTask extends AsyncTask<Integer, Integer, List<MediaEntity>> {
//
//        @Override
//        protected List<MediaEntity> doInBackground(Integer... params) {
//            return new LocalMediaDaoHelper(MediaSelectActivity.this).queryImageMedia();
//        }
//
//        @Override
//        protected void onPostExecute(List<MediaEntity> results) {
//            if (config.openCamera) {
//                MediaEntity entity = new MediaEntity();
//                entity.setCamera(true);
//                items.add(entity);
//            }
//            items.addAll(results);
//            adapter.setItems(items);
//            adapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.tv_complete) {
            onResult(selectedList);
        }else if(id == R.id.tv_title){
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                if (items != null && items.size() > 0) {
                    folderWindow.showAsDropDown(topBar);
                    folderWindow.notifyDataCheckedStatus(selectedList);
                }
            }
        }

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
            }else {
                previewHolder.setData(items);
            }
            previewHolder.show(position);
        }
    }

    private void startCamera() {
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
                            if (config.camera) {
                                closeActivity();
                            }
                            Toast.makeText(getApplicationContext(), "拍照需要相机权限",
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
            LocalMedia mediaEntity;
            switch (requestCode) {
                case MediaConfig.REQUEST_CAMERA:
                    final File file = new File(cameraPath);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    int degree = MediaFileUtils.readPictureDegree(file.getAbsolutePath());
                    rotateImage(degree, file);
                    mediaEntity = new LocalMedia();
                    mediaEntity.setPath(cameraPath);
                    mediaEntity.setChecked(true);
                    //添加到列表并选中

                    if (config.camera) {
                        items.add(0, mediaEntity);
                        selectedList.add(mediaEntity);
                        onResult(selectedList);
                    } else {
                        items.add(1, mediaEntity);
                        if (selectedList.size() < config.maxSelectNum) {
                            selectedList.add(mediaEntity);
                        } else if (config.maxSelectNum == 1 && selectedList.size() == 1) {
                            LocalMedia entity = selectedList.get(0);
                            int index = items.indexOf(entity);
                            if (index >= 0) {
                                items.get(index).setChecked(false);
                            }
                            selectedList.remove(0);
                            selectedList.add(mediaEntity);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        Object tag = compoundButton.getTag();
        if (tag instanceof LocalMedia) {
            if (isChecked) {
                //已到最大个数
                if (selectedList.size() == config.maxSelectNum && 1 != config.maxSelectNum) {
                    compoundButton.setOnCheckedChangeListener(null);
                    compoundButton.setChecked(false);
                    compoundButton.setOnCheckedChangeListener(this);
                    ((LocalMedia) tag).setChecked(false);
                    Toast.makeText(this, "你最多只能选择" + config.maxSelectNum + "张照片", Toast.LENGTH_SHORT).show();
                    return;
                    //单选
                } else if (selectedList.size() == config.maxSelectNum && 1 == config.maxSelectNum) {
                    clearSelected();
                }
                selectedList.add((LocalMedia) tag);
                updateItems((LocalMedia) tag, isChecked);
            } else {
                selectedList.remove(tag);
                updateItems((LocalMedia) tag, isChecked);
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
    private void updateItems(LocalMedia entity, boolean isSelected) {
        int index = items.indexOf(entity);
        if (index >= 0) {
            items.get(index).setChecked(isSelected);
        }
    }

    private void clearSelected() {
        if (selectedList != null) {
            for (LocalMedia entity : selectedList) {
                int indexOf = items.indexOf(entity);
                if (indexOf >= 0) {
                    items.get(indexOf).setChecked(false);
                    adapter.notifyItemChanged(indexOf);
                }
            }
            selectedList.clear();
        }
    }

    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        items.clear();
        if (config.openCamera) {
            LocalMedia entity = new LocalMedia();
            entity.setCamera(true);
            items.add(entity);
        }
        items.addAll(images);
        tvTitle.setText(folderName);
        adapter.notifyDataSetChanged();
        folderWindow.dismiss();
    }


    private class PreviewViewHolder implements ViewPager.OnPageChangeListener {

        ViewPager viewPager;
        TextView imagePosition;
        ImageView frameBack;

        PagerAdapter pagerAdapter;

        List<LocalMedia> list;
        int size;

        public PreviewViewHolder(List<LocalMedia> list) {
            this.list = list;
            viewPager = previewFrame.findViewById(R.id.view_pager);
            viewPager.addOnPageChangeListener(this);
            imagePosition = previewFrame.findViewById(R.id.image_position);
            frameBack = previewFrame.findViewById(R.id.frame_back);
            frameBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    previewFrame.setVisibility(View.GONE);
                }
            });
            size = config.openCamera ? list.size() - 1 : list.size();
        }

        public void setData(List<LocalMedia> list){
            this.list = list;
            size = config.openCamera ? list.size() - 1 : list.size();
        }

        public void show(int index) {
            if (pagerAdapter == null) {
                pagerAdapter = new ImagePreviewAdapter(getSupportFragmentManager(),
                        getStringList(list));
                viewPager.setAdapter(pagerAdapter);
            }

            viewPager.setCurrentItem(index, false);
            imagePosition.setText(String.format("%d/%d", config.openCamera ? index : index + 1, size));
            previewFrame.setVisibility(View.VISIBLE);
        }

        /**
         * 获取图片List列表
         *
         * @param list 数据源
         * @return list集合
         */
        public List<String> getStringList(List<LocalMedia> list) {
            List<String> resultList = new ArrayList<>(list.size());
            for (LocalMedia data : list) {
                resultList.add(data.getPath());
            }
            return resultList;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            imagePosition.setText(String.format("%d/%d", config.openCamera ? position : position + 1, size));

        }

        @Override
        public void onPageScrollStateChanged(int state) {

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
            ImagePreviewFragment imagePreviewFragment = newInstance(data.get(position), config.gesture);
            return imagePreviewFragment;
        }

    }
}
