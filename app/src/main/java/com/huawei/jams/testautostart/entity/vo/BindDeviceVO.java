package com.huawei.jams.testautostart.entity.vo;

import java.io.Serializable;

/**
 * <p>文件描述：绑定设备返回<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/6/3<p>
 * <p>更新时间：2020/6/3<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class BindDeviceVO extends ErrorCode implements Serializable {
    private static final long serialVersionUID = 2884491148162865166L;
    private String cabinetNumber;
    private String cabinetPassword;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getCabinetNumber() {
        return cabinetNumber;
    }

    public void setCabinetNumber(String cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }

    public String getCabinetPassword() {
        return cabinetPassword;
    }

    public void setCabinetPassword(String cabinetPassword) {
        this.cabinetPassword = cabinetPassword;
    }

    @Override
    public String toString() {
        return "BindDeviceVO{" +
                "cabinetNumber='" + cabinetNumber + '\'' +
                ", cabinetPassword='" + cabinetPassword + '\'' +
                ", errcode=" + errcode +
                '}';
    }
}
