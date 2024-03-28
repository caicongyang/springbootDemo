package com.caicongyang.stock.domain;

import java.io.Serializable;

public class MuiltStremKlineTickerEvent implements Serializable {

    private String stream;

    private KlineTickerEvent data;

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }


    public KlineTickerEvent getData() {
        return data;
    }

    public void setData(KlineTickerEvent data) {
        this.data = data;
    }
}
