
package com.yxytech.parkingcloud.baselibrary.http.common;

import android.support.annotation.StringRes;
import com.yxytech.parkingcloud.baselibrary.R;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;

public class ErrorCode {
    public static final int TOKEN_NOT_EXIST = 1000;
    public static final int TOKEN_INVALID = 1001;

    public static final int TOKEN_PAST = -403;
    public static final int RESPONSE_SUCCESS = 200;
    public static final int RESPONSE_NO_CONTENT = 204;
    public static final int RESPONSE_FAILED = 400;
    public static final int PARSE_JSON_ERROR =406;
    /**
     * request success
     */
    public static final int SUCCESS = 0;

    public static final int REQUEST_FAILED = -1;
    /**
     * 登录状态失效
     */
    public static final int INVALID_LOGIN_STATUS = -1001;

    public static final int VERIFY_CODE_ERROR = 110011;

    public static final int VERIFY_CODE_EXPIRED = 110010;

    public static final int ACCOUNT_NOT_REGISTER = 110009;

    public static final int PASSWORD_ERROR = 110012;

    /**
     * Wrong old password
     */
    public static final int OLD_PASSWORD_ERROR = 110015;

    public static final int USER_REGISTERED = 110006;

    public static final int PARAMS_ERROR = 19999;
    /**
     * 异地登录
     */
    public static final int REMOTE_LOGIN = 91011;

    public static String getErrorMessage(int errorCode) {
        return getErrorMessage(errorCode, "");
    }

    /**
     * get error message with error code
     *
     * @param errorCode error code
     * @return error message
     */
    public static String getErrorMessage(int errorCode, String errorMsg) {
        String message;
        switch (errorCode) {
            case REQUEST_FAILED:
                message = getString(R.string.request_error) + errorCode + ",Error Message:" + errorMsg;
                break;
            case VERIFY_CODE_ERROR:
                message = getString(R.string.verify_code_error);
                break;
            case INVALID_LOGIN_STATUS:
                message = getString(R.string.invalid_status);
                break;
            case VERIFY_CODE_EXPIRED:
                message = getString(R.string.verify_code_expired);
                break;
            case ACCOUNT_NOT_REGISTER:
                message = getString(R.string.not_register);
                break;
            case PASSWORD_ERROR:
                message = getString(R.string.wrong_pwd_username);
                break;
            case USER_REGISTERED:
                message = getString(R.string.user_registered);
                break;
            case OLD_PASSWORD_ERROR:
                message = getString(R.string.wrong_password);
                break;
            case PARAMS_ERROR:
                message = getString(R.string.parameters_exception);
                break;
            case REMOTE_LOGIN:
                message = getString(R.string.remote_login);
                break;
            default:
                message = getString(R.string.request_error) + errorCode;
                break;
        }
        return message;
    }

    private static String getString(@StringRes int resId) {
        return BaseApplication.getAppContext().getString(resId);
    }

}
