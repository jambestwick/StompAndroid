package com.huawei.jams.testautostart.entity.vo;

import java.io.Serializable;

public class AlarmPropVO implements Serializable {
    private static final long serialVersionUID = 5916051762317094626L;
    private long  intervalTime;
    private  int   intervalNum;

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getIntervalNum() {
        return intervalNum;
    }

    public void setIntervalNum(int intervalNum) {
        this.intervalNum = intervalNum;
    }

    @Override
    public String toString() {
        return "AlarmPropVO{" +
                "intervalTime=" + intervalTime +
                ", intervalNum=" + intervalNum +
                '}';
    }
}
