package com.huawei.jams.testautostart.presenter.inter;

public interface CallBack<T> {
    void onCallBack(int errorCode, String msg, T data);
}
