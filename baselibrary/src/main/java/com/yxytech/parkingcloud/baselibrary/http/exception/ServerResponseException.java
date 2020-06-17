package com.yxytech.parkingcloud.baselibrary.http.exception;


import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

/**
 * 服务器返回的异常
 */
public class ServerResponseException extends RuntimeException {
    public ServerResponseException(int errorCode, String cause) {
//        super(cause);
        super(cause, new Throwable(errorCode + ""));
        LogUtil.e("ServerResponseException", Thread.currentThread().getName() + ",服务器响应失败，错误码：" + errorCode + "，错误原因：" + cause);


    }
}
