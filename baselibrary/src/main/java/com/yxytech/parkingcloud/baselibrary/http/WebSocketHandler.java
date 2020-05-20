package com.yxytech.parkingcloud.baselibrary.http;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.yxytech.parkingcloud.baselibrary.http.common.RetrofitService;
import com.yxytech.parkingcloud.baselibrary.http.common.SSLHelper;
import com.yxytech.parkingcloud.baselibrary.utils.Base64Util;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import okhttp3.*;
import okio.BufferedSink;
import okio.ByteString;
import retrofit2.http.POST;

import java.io.IOException;
import java.net.URL;

public class WebSocketHandler extends WebSocketListener {

    private static final String TAG = WebSocketHandler.class.getName();

    private String wsUrl;

    private WebSocket webSocket;

    private ConnectStatus status;

    private OkHttpClient client = new OkHttpClient.Builder()
            .build();

    private WebSocketHandler(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    private WebSocketHandler(Context context, String wsUrl) throws IOException {

        client = RetrofitService.setSSL(context,null,SSLHelper.CLIENT_PRI_KEY,SSLHelper.CLIENT_BKS_PASSWORD).build();
        this.wsUrl = wsUrl;
    }

    private static WebSocketHandler INST;

    public static WebSocketHandler getInstance(String url) {
        if (INST == null) {
            synchronized (WebSocketHandler.class) {
                INST = new WebSocketHandler(url);
            }
        }

        return INST;
    }

    public static WebSocketHandler getInstance(Context context, String url) {
        if (INST == null) {
            synchronized (WebSocketHandler.class) {
                try {
                    INST = new WebSocketHandler(context, url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return INST;
    }

    public ConnectStatus getStatus() {
        return status;
    }

    public void connect() {
        //构造request对象
        Request request = new Request.Builder()
                .url(wsUrl)
                .header("Authorization", Base64Util.encodeBasicAuth("100000000000001", "AAAAAAAAAAAAAAAAAAAA_1"))
                .build();
        webSocket = client.newWebSocket(request, this);
        status = ConnectStatus.Connecting;
    }

    public void reConnect() {
        if (webSocket != null) {
            webSocket = client.newWebSocket(webSocket.request(), this);
        }
    }

    public void send(String text) {
        if (webSocket != null) {
            LogUtil.d(TAG, "send： " + text);
            webSocket.send(text);
        }
    }

    public void cancel() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        LogUtil.d(TAG, "onOpen");
        this.status = ConnectStatus.Open;
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack.onOpen(response);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        LogUtil.d(TAG, "onMessage: " + text);
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack.onMessage(text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        this.status = ConnectStatus.Closing;
        LogUtil.d(TAG, "onClosing");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        LogUtil.d(TAG, "onClosed");
        this.status = ConnectStatus.Closed;
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack.onClose(code, reason);
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        LogUtil.d(TAG, "onFailure: " + t.toString());
        t.printStackTrace();
        this.status = ConnectStatus.Canceled;
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack.onConnectError(t, response);
        }
    }


    private WebSocketCallBack mSocketIOCallBack;

    public void setSocketIOCallBack(WebSocketCallBack callBack) {
        mSocketIOCallBack = callBack;
    }

    public interface WebSocketCallBack {
        void onConnectError(Throwable t, Response response);

        void onClose(int code, String reason);

        void onMessage(String text);

        void onOpen(Response response);
    }

    public void removeSocketIOCallBack() {
        mSocketIOCallBack = null;
    }

    public enum ConnectStatus {
        Connecting, // the initial state of each web socket.
        Open, // the web socket has been accepted by the remote peer
        Closing, // one of the peers on the web socket has initiated a graceful shutdown
        Closed, //  the web socket has transmitted all of its messages and has received all messages from the peer
        Canceled // the web socket connection failed
    }

}
