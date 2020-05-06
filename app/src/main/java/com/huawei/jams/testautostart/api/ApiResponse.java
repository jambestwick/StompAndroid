package com.huawei.jams.testautostart.api;

import java.io.Serializable;
import java.util.List;

public class ApiResponse<T> implements Serializable {
    public final static int SUCCESS = 0;
    public final static int COMMON_BIZ_ERROR = 1;
    public final static int PAY_REQUEST_TIMEOUT_ERROR = 2;
    public final static int PERMISSION_DENIED = 3;
    public final static int FAILED = 4;
    private static final long serialVersionUID = 6779437252779466613L;
    private int code;
    private String message;
    private List<ArgumentInvalidResult> errors;
    private T data;
    private String motorColumn;

    public ApiResponse() {}

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(int code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ArgumentInvalidResult> getErrors() {
        return errors;
    }

    public void setErrors(List<ArgumentInvalidResult> errors) {
        this.errors = errors;
    }

    public String getMotorColumn() {
        return motorColumn;
    }

    public void setMotorColumn(String motorColumn) {
        this.motorColumn = motorColumn;
    }
}
