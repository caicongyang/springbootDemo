package com.caicongyang.stock.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class VolumeGtYesterdayStockDTO implements Serializable {

    private String stockName;

    private String stockCode;

    private Double counter;

    private String tradingDay;

    @ApiModelProperty(value = "申万行业")
    private String swL3;

    @ApiModelProperty(value = "聚宽行业")
    private String jqL2;

    private String zjw;

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Double getCounter() {
        return counter;
    }

    public void setCounter(Double counter) {
        this.counter = counter;
    }

    public String getSwL3() {
        return swL3;
    }

    public void setSwL3(String swL3) {
        this.swL3 = swL3;
    }

    public String getJqL2() {
        return jqL2;
    }

    public void setJqL2(String jqL2) {
        this.jqL2 = jqL2;
    }

    public String getZjw() {
        return zjw;
    }

    public void setZjw(String zjw) {
        this.zjw = zjw;
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay;
    }
}
