package com.caicongyang.stock.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KlineTickerEvent implements Serializable {


    /**
     * {
     * "e":"kline",
     * "E":1700292255140,
     * "s":"BTCUSDT",
     * "k":{
     * "t":1700291700000,
     * "T":1700292599999,
     * "s":"BTCUSDT",
     * "i":"15m",
     * "f":3285663523,
     * "L":3285668753,
     * "o":"36278.01000000",
     * "c":"36312.00000000",
     * "h":"36324.48000000",
     * "l":"36278.00000000",
     * "v":"118.49120000",
     * "n":5231,
     * "x":false,
     * "q":"4301939.32430500",
     * "V":"69.36089000",
     * "Q":"2518032.28040940",
     * "B":"0"
     * }
     * }
     */

    @SerializedName("e")
    private String event;


    @SerializedName("s")
    private String symbol;


    @SerializedName("E")
    private long eventTime;

    @SerializedName("k")
    private KlineTicker klineTickers;


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }


    public KlineTicker getKlineTickers() {
        return klineTickers;
    }

    public void setKlineTickers(KlineTicker klineTickers) {
        this.klineTickers = klineTickers;
    }
}
