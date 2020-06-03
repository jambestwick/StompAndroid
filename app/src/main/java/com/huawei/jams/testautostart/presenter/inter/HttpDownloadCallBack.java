package com.huawei.jams.testautostart.presenter.inter;

public abstract class HttpDownloadCallBack<T> {

    public abstract void onDownLoadSuccess(T t);

    //下载失败回调
    public abstract void onDownLoadFail(Throwable throwable);

    //下载进度监听
    public abstract void onProgress(int progress, long total);

}
