package com.zjrb.daily.mediaselector;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zjrb.daily.mediaselector.config.MediaConfig;
import com.zjrb.daily.mediaselector.entity.LocalMedia;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * media选择统一工厂
 *
 * @author zhengy
 * create at 2018/11/22 下午2:35
 **/
public final class MediaSelector {

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    private MediaSelector(Activity activity) {
        this(activity, null);
    }

    private MediaSelector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private MediaSelector(Activity activity, Fragment fragment) {
        this.mActivity = new WeakReference<>(activity);
        this.mFragment = new WeakReference<>(fragment);
    }
    @CheckResult
    public static MediaSelectonModel create(Activity activity) {
        return new MediaSelectonModel(new MediaSelector(activity));
    }
    @CheckResult
    public static MediaSelectonModel create(Fragment fragment) {
        return new MediaSelectonModel(new MediaSelector(fragment));
    }

    public static Intent putIntentResult(List<LocalMedia> data) {
        return new Intent().putExtra(MediaConfig.EXTRA_RESULT_SELECTION, (Serializable) data);
    }

    public static List<LocalMedia> obtainSelectResult(Intent data) {
        List<LocalMedia> result = new ArrayList<>();
        if (data != null) {
            result = (List<LocalMedia>) data.getSerializableExtra(MediaConfig.EXTRA_RESULT_SELECTION);
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
