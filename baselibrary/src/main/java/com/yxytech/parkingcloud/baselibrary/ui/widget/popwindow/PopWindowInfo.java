package com.yxytech.parkingcloud.baselibrary.ui.widget.popwindow;

import java.util.List;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/4<p>
 * <p>更新时间：2019/6/4<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class PopWindowInfo {
    private String key;
    private String value;

    public PopWindowInfo() {
    }

    public PopWindowInfo(String key, String value) {
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

    public static PopWindowInfo constructAllFirst() {
        PopWindowInfo popWindowInfo = new PopWindowInfo();
        popWindowInfo.setKey(" ");
        popWindowInfo.setValue("全部");
        return popWindowInfo;
    }
}
