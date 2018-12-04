package com.zjrb.daily.mediaselector;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.v4.app.Fragment;

import com.zjrb.daily.mediaselector.config.MediaSelectionConfig;
import com.zjrb.daily.mediaselector.ui.MediaSelectActivity;
import com.zjrb.daily.mediaselector.util.DoubleUtils;

/**
* config配置
* @author zhengy
* create at 2018/11/22 下午2:58
**/
public class MediaSelectonModel {
    private MediaSelectionConfig selectionConfig;
    private MediaSelector selector;

    public MediaSelectonModel(MediaSelector selector){
        this.selector = selector;
        selectionConfig = MediaSelectionConfig.getCleanInstance();
    }

    /**
     * 如果直接打开相机，默认是单选模式，拍照后会直接返回（有设置压缩会先压缩文件再返回）
     * @param camera
     * @return
     */
    @CheckResult
    public MediaSelectonModel camera(boolean camera){
        selectionConfig.camera = camera;
        return this;
    }
    @CheckResult
    public MediaSelectonModel maxSelectNum(int maxSelectNum){
        selectionConfig.maxSelectNum = maxSelectNum;
        return this;
    }
    @CheckResult
    public MediaSelectonModel isShowSelectedNum(boolean isShowSelectedNum){
        selectionConfig.isShowSelectedNum = isShowSelectedNum;
        return this;
    }
    @CheckResult
    public MediaSelectonModel canPreview(boolean canPreview){
        selectionConfig.canPreview = canPreview;
        return this;
    }
    @CheckResult
    public MediaSelectonModel openCamera(boolean openCamera){
        selectionConfig.openCamera = openCamera;
        return this;
    }

    @CheckResult
    public MediaSelectonModel imageSpanCount(int imageSpanCount) {
        selectionConfig.imageSpanCount = imageSpanCount;
        return this;
    }
    @CheckResult
    public MediaSelectonModel isZoomAnim(boolean zoomAnim) {
        selectionConfig.zoomAnim = zoomAnim;
        return this;
    }

    @CheckResult
    public MediaSelectonModel compress(boolean isCompress) {
        selectionConfig.isCompress = isCompress;
        return this;
    }
    @CheckResult
    public MediaSelectonModel minimumCompressSize(int size) {
        selectionConfig.minimumCompressSize = size;
        return this;
    }
    @CheckResult
    public MediaSelectonModel synOrAsy(boolean synOrAsy) {
        selectionConfig.synOrAsy = synOrAsy;
        return this;
    }


    @CheckResult
    public MediaSelectonModel compressSavePath(String path) {
        selectionConfig.compressSavePath = path;
        return this;
    }
    @CheckResult
    public MediaSelectonModel imageFormat(String suffixType) {
        selectionConfig.suffixType = suffixType;
        return this;
    }
    @CheckResult
    public MediaSelectonModel setOutputCameraPath(String outputCameraPath) {
        selectionConfig.outputCameraPath = outputCameraPath;
        return this;
    }
    @CheckResult
    public MediaSelectonModel gesture(boolean gesture) {
        selectionConfig.gesture = gesture;
        return this;
    }

    public void forResult(int requestCode){
        if(!DoubleUtils.isFastDoubleClick()){
            Activity activity = selector.getActivity();
            if(activity == null){
                return;
            }
            Intent intent = new Intent(activity, MediaSelectActivity.class);
            Fragment fragment = selector.getFragment();
            //此处是为了fragment能够有效接收到回调
            if(fragment != null){
                fragment.startActivityForResult(intent, requestCode);
            }else {
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }
}
