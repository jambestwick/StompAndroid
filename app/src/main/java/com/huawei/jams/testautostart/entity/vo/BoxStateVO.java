package com.huawei.jams.testautostart.entity.vo;

import java.io.Serializable;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/6/4<p>
 * <p>更新时间：2020/6/4<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class BoxStateVO implements Serializable {
    private static final long serialVersionUID = 180099457561533167L;
    private int eventcode;

    public int getEventcode() {
        return eventcode;
    }

    public void setEventcode(int eventcode) {
        this.eventcode = eventcode;
    }
    public enum EnumEventCode{
        OPEN(0,"开门"),CLOSE(1,"关门");
        public int key;
        public String value;

        EnumEventCode(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
