package com.huawei.jams.testautostart.entity.vo;

import java.io.Serializable;

public class ServerHeartBeatVO implements Serializable {
    private static final long serialVersionUID = -1763420813523775237L;
    private String heart;

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }
}
