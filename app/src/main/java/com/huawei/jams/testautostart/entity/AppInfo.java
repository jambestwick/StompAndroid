package com.huawei.jams.testautostart.entity;

import com.huawei.jams.testautostart.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Table(database = AppDataBase.class, name = "tb_app_info")
public class AppInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 2797671272074157356L;
    @PrimaryKey
    private UUID uuid;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "url")
    private String url;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "force_update")
    private boolean forceUpdate;

    public AppInfo(UUID uuid, String appVersion, Date createTime, String url, String filePath, boolean forceUpdate) {
        this.uuid = uuid;
        this.appVersion = appVersion;
        this.createTime = createTime;
        this.url = url;
        this.filePath = filePath;
        this.forceUpdate = forceUpdate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "uuid=" + uuid +
                ", appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", createTime=" + createTime +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", forceUpdate=" + forceUpdate +
                '}';
    }

    public enum EnumForceUpdate {
        FORCE("1", true), NOT_FORCE("2", false);
        public String key;
        public boolean value;

        EnumForceUpdate(String key, boolean value) {
            this.key = key;
            this.value = value;
        }
    }
}
