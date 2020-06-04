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
public class OpenBoxVO extends ErrorCode implements Serializable {
    private static final long serialVersionUID = 4139812193558115503L;
    private String boxNumber;

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }
}
