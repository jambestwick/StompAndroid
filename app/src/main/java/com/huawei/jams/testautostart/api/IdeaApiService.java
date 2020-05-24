package com.huawei.jams.testautostart.api;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import java.util.Map;

/**
 * Created by dell on 2017/4/1.
 */

public interface IdeaApiService {


    public static final String SERVER_HOST = "https://www.metalcar.cn";

    public static final String WS_URI = "wss://www.metalcar.cn:443/ws/endpoint";


    public static final String APP_QUERY_VERSION = "/app/queryVersion";
    public static final String ADV_QUERY_VERSION = "/advise/queryVersion";
    public static final String DEVICE_BIND = "/device/bind";
    public static final String DEVICE_UPDATE_BOX_STATE = "/device/update/boxState";
    public static final String DEVICE_OPEN_BOX = "/device/openBox";
    public static final String DEVICE_QUERY_ALARM_PROP = "/device/query/alarmProp";


    /**
     * 绑定设备
     *
     * @return
     */
    @POST("/device/bind")
    Observable<ApiResponse> bindDevice(@Body Map reqMap);

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
     * 查询广告版本文件
     **/
    @POST("/queryAdvVersion")
    Observable<ApiResponse> queryAdvVersion();


    /**
     * 查询应用信息
     */
    @POST("/queryAppInfo")
    Observable<ApiResponse> queryAppInfo();


    /**
     * 下载文件
     **/
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
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
