package com.zjrb.daily.mediaselector;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
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

    public MediaSelectonModel getModel(){
        return new MediaSelectonModel(this);
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
