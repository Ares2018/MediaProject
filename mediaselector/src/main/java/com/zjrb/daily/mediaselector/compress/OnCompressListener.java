package com.zjrb.daily.mediaselector.compress;

import com.zjrb.daily.mediaselector.entity.MediaEntity;

import java.util.List;

public interface OnCompressListener {

    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(List<MediaEntity> list);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(Throwable e);
}
