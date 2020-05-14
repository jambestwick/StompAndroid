package com.huawei.jams.testautostart.entity;

import com.huawei.jams.testautostart.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Table(database = AppDataBase.class, name = "tb_device_info")
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 3547764664305244570L;
    @PrimaryKey
    private UUID uuid;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "box_id")
    private String boxId;

    @Column(name = "box_state")
    private String boxState;

    @Column(name = "create_time")
    private Date createTime;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBoxState() {
        return boxState;
    }

    public void setBoxState(String boxState) {
        this.boxState = boxState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "uuid=" + uuid +
                ", deviceName='" + deviceName + '\'' +
                ", boxId='" + boxId + '\'' +
                ", boxState='" + boxState + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public enum EnumBoxState {
        OPEN("1", "打开"),
        CLOSE("2", "关闭");
        private String key;
        private String value;

        EnumBoxState(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static EnumBoxState getEnumByKey(String key) {
            if (key == null || key.equals("")) {
                return null;
            }

            for (EnumBoxState enumBoxState : EnumBoxState.values()) {
                if (enumBoxState.getKey().equals(key)) {
                    return enumBoxState;
                }
            }

            return null;
        }

        public static EnumBoxState getEnumByValue(String value) {
            if (value == null || value.equals("")) {
                return null;
            }

            for (EnumBoxState enumBoxState : EnumBoxState.values()) {
                if (enumBoxState.getValue().equals(value)) {
                    return enumBoxState;
                }
            }

            return null;
        }
    }
}
