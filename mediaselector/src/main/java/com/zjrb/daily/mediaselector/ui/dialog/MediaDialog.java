package com.zjrb.daily.mediaselector.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.zjrb.daily.mediaselector.R;


public class MediaDialog extends Dialog {
    public Context context;

    public MediaDialog(Context context) {
        super(context, R.style.media_alert_dialog);
        this.context = context;
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
//        window.setWindowAnimations(R.style.DialogWindowStyle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_alert_dialog);
    }
}