package com.huawei.jams.testautostart.api;


import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.entity.vo.BindDeviceVO;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import java.util.Map;

/**
 * Created by dell on 2017/4/1.
 */

public interface IdeaApiService {


    //    String SERVER_HOST = "https://api.metalcar.cn";
//    String WS_URI = "wss://api.metalcar.cn:443/ws/endpoint";
//    String SERVER_HOST = "https://dev.metalcar.cn";
//    String WS_URI = "wss://dev.metalcar.cn/ws/endpoint";
    String SERVER_HOST = "https://pre.metalcar.cn";
    String WS_URI = "wss://pre.metalcar.cn/ws/endpoint";

    //String WS_URI = "ws://121.40.165.18:8800";
    //String WS_URI = "wss://openhw.work.weixin.qq.com:443";


    String APP_QUERY_VERSION = "/user/queue/apk";
    String ADV_QUERY_VERSION = "/user/queue/ad";
    String DEVICE_UPDATE_BOX_STATE = "/ws/cabinet/box-event";
    String DEVICE_UPDATE_BOX_STATE_RECEIVE = "/user/queue/box-event";
    String DEVICE_OPEN_BOX = "/ws/cabinet/ask-for-open-box";
    String DEVICE_OPEN_BOX_RECEIVE = "/user/queue/ask-for-open-box";


    /**
     * 绑定设备
     *
     * @return
     */
    @GET("/cabinet/init")
    Observable<BindDeviceVO> bindDevice(@Query("key") String sixCode);

    /**
     * 获取验证码
     *
     * @return
     */
    @GET("/efss/register/sms/{phoneNum}")
    Observable<String> sms(@Path("phoneNum") String phoneNum);

    /**
     * 登录
     *
     * @param user
     * @return
     */
//    @POST("/webUILogin")
//    Observable<UserLoginResp> login(@Body UserLoginReq user);


    /**
     * /**
     *
     * @param url 下载地址
     *            大文件官方建议用 @Streaming 来进行注解，不然会出现IO异常，小文件可以忽略不注入
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@NonNull @Url String url);

//
//    @Multipart
//    @POST("/efss/attendance/uploadUserCheck")
//    Observable<AttendanceBeanResp> uploadUserCheck(
//            @Part MultipartBody.Part file,
//            @QueryMap Map<String, Object> reqMap);
//
//    @POST("/efss/attendance/userDayList")
//    Observable<AttendanceListResp> getUserDayCheckList(@Body Map reqMap);
//
//    @POST("/efss/attendance/userHistoryCheck")
//    Observable<AttendanceListResp> getUserHistoryCheckList(@Body Map reqMap);
//
//    /**
//     * 上传图片
//     *
//     * @param part
//     */
//    @Multipart
//    @POST("/efss/imageManager/upload")
//    Observable<BaseResponse> uploadImage(@Part MultipartBody.Part part);

}
