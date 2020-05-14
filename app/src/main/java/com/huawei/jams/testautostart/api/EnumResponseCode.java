package com.huawei.jams.testautostart.api;

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

}
