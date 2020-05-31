package com.yxytech.parkingcloud.baselibrary.http.common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.yxytech.parkingcloud.baselibrary.R;
import com.yxytech.parkingcloud.baselibrary.http.exception.ServerResponseException;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by zhpan on 2017/4/18.
 */

public abstract class DefaultObserver<T> implements Observer<T> {

    private static final String TAG = DefaultObserver.class.getName();

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T response) {
        onSuccess(response);
        onFinish();
    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
            LogUtil.e(TAG, "onError" + Log.getStackTraceString(e));
            if (e instanceof HttpException) {     //   HTTP错误
                onException(ExceptionReason.BAD_NETWORK);
            } else if (e instanceof ConnectException
                    || e instanceof UnknownHostException) {   //   连接错误
                onException(ExceptionReason.CONNECT_ERROR);
            } else if (e instanceof InterruptedIOException) {   //  连接超时
                onException(ExceptionReason.CONNECT_TIMEOUT);
            } else if (e instanceof JsonParseException
                    || e instanceof JSONException
                    || e instanceof ParseException) {   //  解析错误
                onException(ExceptionReason.PARSE_ERROR);
            } else if (e instanceof ServerResponseException) {
                onFail(Integer.parseInt(e.getCause().getMessage()), e.getMessage());
            } else {
                onException(ExceptionReason.UNKNOWN_ERROR);
            }
            onFinish();
        }

    }



    @Override
    public void onComplete() {
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response);

    /**
     * 服务器返回数据，但响应码不为200
     *
     */
    /**
     * 服务器返回数据，但响应码不为1000
     */
    public void onFail(int errorCode, String cause) {
        LogUtil.d(TAG, "onFail---------------" + cause);
//        if (errorCode == ErrorCode.TOKEN_PAST) {
//            try {
//                Intent intent = new Intent(context, Class.forName("com.huawei.jams.testautostart.view.activity.WelcomeActivity"));
//                //intent.putExtra("isRefresh", true);
//                context.startActivity(intent);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        ToastUtil.showToast(BaseApplication.getAppContext(), cause);
    }

    public void onFinish(){}

    /**
     * 请求异常
     *
     * @param reason
     */
    public void onException(ExceptionReason reason) {
        switch (reason) {
            case CONNECT_ERROR:
                ToastUtil.showToast(BaseApplication.getAppContext(), R.string.connect_error);
                break;

            case CONNECT_TIMEOUT:
                ToastUtil.showToast(BaseApplication.getAppContext(), R.string.connect_timeout);
                break;

            case BAD_NETWORK:
                ToastUtil.showToast(BaseApplication.getAppContext(), R.string.bad_network);
                break;

            case PARSE_ERROR:
                ToastUtil.showToast(BaseApplication.getAppContext(), R.string.parse_error);
                break;

            case UNKNOWN_ERROR:
            default:
                ToastUtil.showToast(BaseApplication.getAppContext(), R.string.unknown_error);
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
