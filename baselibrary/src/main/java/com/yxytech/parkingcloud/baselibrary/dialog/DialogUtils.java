package com.yxytech.parkingcloud.baselibrary.dialog;

import android.app.Activity;
import android.content.Context;

import com.yxytech.parkingcloud.baselibrary.R;

/**
 * Created by zhpan on 2017/5/26.
 * Description:
 */

public class DialogUtils {
    //  加载进度的dialog
    private CustomProgressDialog mProgressDialog;

    /**
     * 显示ProgressDialog
     */
    public void showProgress(Activity context, String msg) {
        if (context == null || context.isFinishing()) {
            return;
        }
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
    public void showProgress(Activity context) {
        if (context == null || context.isFinishing()) {
            return;
        }
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
        if (null != mProgressDialog) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    }
}
