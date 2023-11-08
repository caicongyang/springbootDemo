package com.caicongyang.stock.domain;

import java.io.Serializable;

public class QyWeixinMsg implements Serializable {

    private String msgtype;


    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
