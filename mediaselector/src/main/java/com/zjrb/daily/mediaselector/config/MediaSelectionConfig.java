package com.zjrb.daily.mediaselector.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.zjrb.daily.mediaselector.util.PictureFileUtils;

/**
* media 选择可配置项
* @author zhengy
* create at 2018/11/22 下午2:57
**/
public class MediaSelectionConfig implements Parcelable {

    public int maxSelectNum;
    public boolean isShowSelectedNum;
    public boolean canPreview;
    public boolean openCamera;
    //后缀
    public String suffixType;
    //自定义照片存储路径
    public String outputCameraPath;

    public int imageSpanCount;


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
        this.maxSelectNum = in.readInt();
        this.isShowSelectedNum = in.readByte() != 0;
        this.canPreview = in.readByte() != 0;
        this.openCamera = in.readByte() != 0;
        this.suffixType = in.readString();
        this.outputCameraPath = in.readString();
        this.imageSpanCount = in.readInt();
    }

    private void reset(){
        maxSelectNum = 9;
        isShowSelectedNum = true;
        canPreview = true;
        openCamera = false;
        suffixType = PictureFileUtils.POSTFIX;
        outputCameraPath = "";
        imageSpanCount = 3;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxSelectNum);
        dest.writeByte(this.isShowSelectedNum ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canPreview ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openCamera ? (byte) 1 : (byte) 0);
        dest.writeString(this.suffixType);
        dest.writeString(this.outputCameraPath);
        dest.writeInt(this.imageSpanCount);
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
