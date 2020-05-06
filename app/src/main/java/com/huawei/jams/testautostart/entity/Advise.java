package com.huawei.jams.testautostart.entity;

import com.huawei.jams.testautostart.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Table(database = AppDataBase.class, name = "tb_advise")
public class Advise extends BaseModel implements Serializable {
    private static final long serialVersionUID = -2039105766997712648L;


    @PrimaryKey
    private UUID uuid;

    @Column(name = "adv_no")
    private String advNo;

    @Column(name = "adv_version")
    private String advVersion;
    @Column(name = "adv_date")
    private Date advDate;//广告时间

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "createUser")
    private String createUser;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "modify_user")
    private String modifyUser;
    @Column(name = "modify_time")
    private Date modifyTime;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAdvNo() {
        return advNo;
    }

    public void setAdvNo(String advNo) {
        this.advNo = advNo;
    }

    public String getAdvVersion() {
        return advVersion;
    }

    public void setAdvVersion(String advVersion) {
        this.advVersion = advVersion;
    }

    public Date getAdvDate() {
        return advDate;
    }

    public void setAdvDate(Date advDate) {
        this.advDate = advDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "Advise{" +
                "uuid=" + uuid +
                ", advNo='" + advNo + '\'' +
                ", advVersion='" + advVersion + '\'' +
                ", advDate=" + advDate +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", valid=" + valid +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", modifyUser='" + modifyUser + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }


}
