package com.yxytech.parkingcloud.baselibrary.dialog;

import android.content.Context;

import com.yxytech.parkingcloud.baselibrary.R;

/**
 * Created by Water on 2018/3/29.
 */

public class DialogUtils {
    //  加载进度的dialog
    private CustomProgressDialog mProgressDialog;

    /**
     * 显示ProgressDialog
     */
    public void showProgress(Context context, String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog.Builder(context)
                    .setTheme(R.style.ProgressDialogStyle)
                    .setMessage(msg)
                    .build();
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 显示ProgressDialog
     */
    public void showProgress(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog.Builder(context)
                    .setTheme(R.style.ProgressDialogStyle)
                    .build();
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 取消ProgressDialog
     */
    public void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
