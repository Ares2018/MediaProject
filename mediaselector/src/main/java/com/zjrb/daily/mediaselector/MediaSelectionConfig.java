package com.zjrb.daily.mediaselector;

import android.os.Parcel;
import android.os.Parcelable;
/**
* media 选择可配置项
* @author zhengy
* create at 2018/11/22 下午2:57
**/
public class MediaSelectionConfig implements Parcelable {

    public int maxSelectNum;
    public boolean isShowSelectedNum;
    public boolean canPreview;


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
    }

    private void reset(){
        maxSelectNum = 9;
        isShowSelectedNum = true;
        canPreview = true;
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
