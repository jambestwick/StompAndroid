package com.huawei.jams.testautostart.api;

import com.huawei.jams.testautostart.entity.DeviceInfo;

public enum EnumResponseCode {
    SUCCESS(0, "SUCCESS"),
    COMMON_BIZ_ERROR(1, "COMMON_BIZ_ERROR"),
    TIMEOUT_ERROR(2, "TIMEOUT_ERROR"),
    PERMISSION_DENIED(3, "PERMISSION_DENIED"),
    FAILED(4, "FAILED");
    private int key;
    private String value;

    EnumResponseCode(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static EnumResponseCode getEnumByKey(int key) {
        for (EnumResponseCode enumResponseCode : EnumResponseCode.values()) {
            if (enumResponseCode.getKey() == key) {
                return enumResponseCode;
            }
        }

        return null;
    }

    public static EnumResponseCode getEnumByValue(String value) {
        if (value == null || value.equals("")) {
            return null;
        }

        for (EnumResponseCode enumBoxState : EnumResponseCode.values()) {
            if (enumBoxState.getValue().equals(value)) {
                return enumBoxState;
            }
        }

        return null;
    }


}
