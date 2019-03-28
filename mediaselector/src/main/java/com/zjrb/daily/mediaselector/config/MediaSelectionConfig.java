package com.zjrb.daily.mediaselector.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.zjrb.daily.mediaselector.util.MediaFileUtils;

/**
* media 选择可配置项
* @author zhengy
* create at 2018/11/22 下午2:57
**/
public class MediaSelectionConfig implements Parcelable {
    public int mimeType;
    //直接启用相机
    public boolean camera;
    public int maxSelectNum;
    public boolean isShowSelectedNum;
    public boolean canPreview;
    //显示拍照按钮
    public boolean openCamera;
    //后缀
    public String suffixType;
    //自定义照片存储路径
    public String outputCameraPath;

    public int imageSpanCount;
    public boolean zoomAnim;

    //是否可压缩
    public boolean isCompress;
    //最小可压缩尺寸
    public int minimumCompressSize;
    //同步或者异步压缩
    public boolean synOrAsy;
    public String compressSavePath;
    //预览页面手势操作：缩放，旋转
    public boolean gesture;
    public boolean isGif;


    private static final class InstanceHolder {
        private static final MediaSelectionConfig INSTANCE = new MediaSelectionConfig();
    }

    public static MediaSelectionConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static MediaSelectionConfig getCleanInstance(){
        MediaSelectionConfig selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }


    public MediaSelectionConfig() {
    }

    public MediaSelectionConfig(Parcel in) {
        this.mimeType = in.readInt();
        this.camera = in.readByte() != 0;
        this.maxSelectNum = in.readInt();
        this.isShowSelectedNum = in.readByte() != 0;
        this.canPreview = in.readByte() != 0;
        this.openCamera = in.readByte() != 0;
        this.suffixType = in.readString();
        this.outputCameraPath = in.readString();
        this.imageSpanCount = in.readInt();
        this.zoomAnim = in.readByte() != 0;
        this.isCompress = in.readByte() != 0;
        this.minimumCompressSize = in.readInt();
        this.synOrAsy = in.readByte() != 0;
        this.compressSavePath = in.readString();
        this.gesture = in.readByte() != 0;
        this.isGif = in.readByte() != 0;
    }

    private void reset(){
        mimeType = MediaConfig.TYPE_IMAGE;
        camera = false;
        maxSelectNum = 9;
        isShowSelectedNum = true;
        canPreview = true;
        openCamera = false;
        suffixType = MediaFileUtils.POSTFIX;
        outputCameraPath = "";
        imageSpanCount = 3;
        zoomAnim = false;
        isCompress = false;
        minimumCompressSize = MediaConfig.MAX_COMPRESS_SIZE;
        synOrAsy = true;
        compressSavePath = "";
        gesture = true;
        isGif = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mimeType);
        dest.writeByte(this.camera ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSelectNum);
        dest.writeByte(this.isShowSelectedNum ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canPreview ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openCamera ? (byte) 1 : (byte) 0);
        dest.writeString(this.suffixType);
        dest.writeString(this.outputCameraPath);
        dest.writeInt(this.imageSpanCount);
        dest.writeByte(this.zoomAnim ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCompress ? (byte) 1 : (byte) 0);
        dest.writeInt(this.minimumCompressSize);
        dest.writeByte(this.synOrAsy ? (byte) 1 : (byte) 0);
        dest.writeString(this.compressSavePath);
        dest.writeByte(this.gesture ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGif ? (byte) 1 : (byte) 0);
    }

    public static final Creator<MediaSelectionConfig> CREATOR = new Creator<MediaSelectionConfig>() {
        @Override
        public MediaSelectionConfig createFromParcel(Parcel in) {
            return new MediaSelectionConfig(in);
        }

        @Override
        public MediaSelectionConfig[] newArray(int size) {
            return new MediaSelectionConfig[size];
        }
    };
}
