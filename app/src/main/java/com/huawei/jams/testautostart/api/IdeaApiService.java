package com.huawei.jams.testautostart.api;


import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.*;

/**
 * Created by dell on 2017/4/1.
 */

public interface IdeaApiService {


    public static final String SERVER_HOST = "http://39.104.57.202:8102";

    public static final String WS_URI = "ws://10.0.2.2:8080/example-endpoint/websocket";


    public static final String QUERY_APP_VERSION="/app/queryVersion";

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


    @POST("/app/queryVersion")
    Completable queryAppVersion(@Query("msg") String message);

    @POST("hello-convert-and-send")
    Completable sendRestEcho(@Query("msg") String message);


//    /***
//     * 用户解绑推送
//     *
//     * **/
//    @POST("/efss/cloudUser/appLogout")
//    Observable<UserLogoutResp> logout(@Body Map reqMap);
//
//    /***
//     * 用户绑定推送
//     *
//     * **/
//    @POST("/efss/cloudUser/appLogin")
//    Observable<UserAppInfoResp> uploadUser(@Body UserAppInfoReq userAppInfoReq);
//
//    /**
//     * 用户注册
//     *
//     * @param registerReq
//     * @return
//     */
//    @POST("/efss/register/mobile")
//    Observable<UserLoginResp> register(@Body UserRegisterReq registerReq);
//
//    /**
//     * 忘记密码
//     *
//     * @param passwordResetReq
//     * @return
//     */
//    @POST("/efss/cloudUser/resetPassword")
//    Observable<PasswordModifyResp> forgetPassword(@Body PasswordModifyReq passwordResetReq);
//
//
//    /**
//     * 修改密码
//     *
//     * @return
//     * @params passwordModifyReq
//     */
//    @POST("/efss/cloudUser/modifyPassword")
//    Observable<PasswordModifyResp> updatePassword(@Body PasswordModifyReq passwordModifyReq);
//
//
//    /**
//     * 接收工单
//     **/
//    @POST("/efss/dispatchRecord/receive")
//    Observable<BaseResponse> updateByWorkOrderId(@Body InspectionDispatchReq dispatchReq);
//
//    /**
//     * 上传已修改的工单
//     */
//    @POST("/efss/dispatchRecord/uploadModify")
//    Observable<BaseResponse> uploadModifyWorkOrder(@Body Map reqMap);
//
//    /***
//     * 查看工单详情和状态
//     * **/
//    @POST("/efss/dispatchRecord/detailAndState")
//    Observable<InspectionDispatchDetailResp> workOrderDetailAndState(@Body Map reqMap);
//
//    /**
//     * 查看工单列表
//     */
//    @POST("/efss/dispatchRecord/workOrderList")
//    Observable<InspectionDispatchListResp> workOrderList(@Body Map reqMap);
//
//    @POST("/efss/test/uploadTerminalIp")
//    Observable<String> socketTest(@Body JSONObject jsonObject);
//
//    /***
//     * 获取泊位列表信息
//     *
//     * */
//    @POST("/efss/berthVehicleState/berthStateList")
//    Observable<BerthStateListResp> berthStateList(@Body Map reqMap);
//
//    /***
//     * 获取泊位详情信息
//     *
//     * */
//    @POST("/efss/berthVehicleState/detail")
//    Observable<BerthStateResp> berthStateDetail(@Body Map reqMap);
//
//
//    @POST("/efss/parkingSpaceInfo/detail")
//    Observable<BaseResponse> hasBerthCode(@Body Map reqMap);
//
//    /**
//     * 获取停车场列表
//     *
//     * @param reqMap (userUuid:"")
//     **/
//    @POST("/efss/parkingInfo/getList")
//    Observable<ParkingNameResp> getParkingList(@Body Map reqMap);
//
//    /***
//     * 获取停泊位状态
//     *  @return 1占用, 2空闲，3未知
//     * **/
//    @GET("/efss/enum/berthParkState")
//    Observable<BaseResponse> getBerthState();
//
//    /**
//     * 获取事件类型
//     *
//     * @return 1入场，2出场
//     **/
//    @GET("/efss/enum/eventType")
//    Observable<BaseResponse> getEventType();
//
//    /**
//     * 获取车牌颜色
//     *
//     * @return 1蓝，2黄，3黑，4白，5绿，6黄绿，7蓝白
//     **/
//    @GET("/efss/enum/plateColor")
//    Observable<BaseResponse> getPlateColor();
//
//    /**
//     * 获取工单状态
//     *
//     * @return 1新派单，2待处理，3已处理，4已审核
//     **/
//    @GET("/efss/enum/workOrderState")
//    Observable<BaseResponse> getOrderState();
//
//    /**
//     * 获取车型
//     *
//     * @return 1小，2大
//     **/
//    @GET("/efss/enum/vehicleType")
//    Observable<BaseResponse> getVehicleType();
//
//    /**
//     * 获取签到类型
//     *
//     * @return 1签到，2签退
//     **/
//    @GET("/efss/enum/checkType")
//    Observable<BaseResponse> getCheckType();
//
//    /**
//     * 获取工单异常类型
//     *
//     * @return NORMAL(" 1 ", " 正常 "),
//     * REPEAT("2", "重复"),
//     * INCOMPLETE("3", "数据不完整"),
//     * PICBLUR("4", "图片模糊"),
//     * INTERFERE("5", "干扰数据"),
//     * NOLLICENSE("6", "无车牌"),
//     * BLOCK("7", "遮挡"),
//     * OTHER("8", "其他"),
//     * PLATE_RELIABLE_LOW("9", "车牌可信度低"),
//     * INOUT_RELIABLE_LOW("10", "出入行为可信度低");
//     **/
//    @GET("/efss/enum/exceptionType")
//    Observable<BaseResponse> getAbnormalType();
//
//    /**
//     * 获取工单操作类型
//     **/
//    @GET("/efss/enum/operationType")
//    Observable<BaseResponse> getOperationType();
//
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
