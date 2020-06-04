package com.huawei.jams.testautostart.entity.vo;

import java.io.Serializable;

public class AppVO extends ErrorCode implements Serializable {
    private static final long serialVersionUID = -6517202888601037493L;
    private String version;
    private String downloadUrl;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
