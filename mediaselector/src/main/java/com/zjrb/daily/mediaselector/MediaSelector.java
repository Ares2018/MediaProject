package com.zjrb.daily.mediaselector;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zjrb.daily.mediaselector.config.MediaConfig;
import com.zjrb.daily.mediaselector.entity.MediaEntity;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
* media选择统一工厂
* @author zhengy
* create at 2018/11/22 下午2:35
**/
public final class MediaSelector {

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    private MediaSelector(Activity activity){
        this(activity, null);
    }

    private MediaSelector(Fragment fragment){
        this(fragment.getActivity(), fragment);
    }

    public MediaSelector(Activity activity, Fragment fragment) {
        this.mActivity = new WeakReference<>(activity);
        this.mFragment = new WeakReference<>(fragment);
    }

    public static MediaSelector create(Activity activity){
        return new MediaSelector(activity);
    }

    public static MediaSelector create(Fragment fragment){
        return new MediaSelector(fragment);
    }

    @CheckResult
    public MediaSelectonModel getModel(){
        return new MediaSelectonModel(this);
    }


    public static Intent putIntentResult(List<MediaEntity> data){
        return new Intent().putExtra(MediaConfig.EXTRA_RESULT_SELECTION, (Serializable) data);
    }

    public static List<MediaEntity> obtainSelectResult(Intent data) {
        List<MediaEntity> result = new ArrayList<>();
        if (data != null) {
            result = (List<MediaEntity>) data.getSerializableExtra(MediaConfig.EXTRA_RESULT_SELECTION);
            if (result == null) {
                result = new ArrayList<>();
            }
            return result;
        }
        return result;
    }

    /**
     * @return Activity.
     */
    @Nullable
    Activity getActivity() {
        return mActivity.get();
    }

    /**
     * @return Fragment.
     */
    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }
}
