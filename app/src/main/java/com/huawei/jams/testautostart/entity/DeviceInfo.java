package com.huawei.jams.testautostart.entity;

import com.huawei.jams.testautostart.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.*;

@Table(database = AppDataBase.class, name = "tb_device_info")
public class DeviceInfo extends BaseModel implements Serializable {

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

    public DeviceInfo() {
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
        OPEN(0, "打开"),
        CLOSE(1, "关闭");
        private int key;
        private String value;

        EnumBoxState(int key, String value) {
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

        public static EnumBoxState getEnumByKey(int key) {

            for (EnumBoxState enumBoxState : EnumBoxState.values()) {
                if (enumBoxState.getKey() == key) {
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

    static int count = 0;

    public static void main(String[] args) {
        List<String> lists = new ArrayList<>();
        lists.add("1123");
        lists.add("1123");
        lists.add("1123");
        lists.add("1123");
        String[] arryas = {"abc", "2", "10", "0"};
        for (String sdd : lists
        ) {

        }


        int a = 1;
        int b = 0;
        int c = a / b;
        System.out.println(c);

//        String[] arryas={"abc","2","10","0"};
//        List<String >list = Arrays.asList(arryas);
//        Collections.sort(list);
//        System.out.println(Arrays.toString(arryas));
//
//        System.out.print("apple".compareTo("banana"));
//        double bbb = Math.PI;
//
//        LinkedList<Integer> list = new LinkedList<>();
//        list.add(5);
//        list.add(1);
//        list.add(10);
//        System.out.println(list);

        String message = "Hello";


    }

    static void print(String message) {
        System.out.print(message);
        message += " ";
    }
}
