package com.zjrb.daily.mediaselector;

import android.app.Activity;
import android.content.Intent;
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

    public MediaSelectonModel maxSelectNum(int maxSelectNum){
        selectionConfig.maxSelectNum = maxSelectNum;
        return this;
    }

    public MediaSelectonModel isShowSelectedNum(boolean isShowSelectedNum){
        selectionConfig.isShowSelectedNum = isShowSelectedNum;
        return this;
    }

    public MediaSelectonModel canPreview(boolean canPreview){
        selectionConfig.canPreview = canPreview;
        return this;
    }

    public MediaSelectonModel openCamera(boolean openCamera){
        selectionConfig.openCamera = openCamera;
        return this;
    }

    public MediaSelectonModel imageSpanCount(int imageSpanCount) {
        selectionConfig.imageSpanCount = imageSpanCount;
        return this;
    }

    public MediaSelectonModel imageFormat(String suffixType) {
        selectionConfig.suffixType = suffixType;
        return this;
    }

    public MediaSelectonModel setOutputCameraPath(String outputCameraPath) {
        selectionConfig.outputCameraPath = outputCameraPath;
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
