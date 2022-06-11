package com.caicongyang.stock.domain;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.time.LocalDate;

@ApiModel(value="异动&30日内涨停对象", description="")
public class TransactionAndLimitStockDTO implements Serializable {

    private String stockCode;

    private LocalDate tradingDay;

    private Double lastDayCompare;

    private Double meanRatio;

    private Double gain;

    private String stockName;

    private String swL3;

    private String jqL2;

    private String zjw;

    private Integer counter;


    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public LocalDate getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(LocalDate tradingDay) {
        this.tradingDay = tradingDay;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
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

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Double getLastDayCompare() {
        return lastDayCompare;
    }

    public void setLastDayCompare(Double lastDayCompare) {
        this.lastDayCompare = lastDayCompare;
    }

    public Double getMeanRatio() {
        return meanRatio;
    }

    public void setMeanRatio(Double meanRatio) {
        this.meanRatio = meanRatio;
    }

    public Double getGain() {
        return gain;
    }

    public void setGain(Double gain) {
        this.gain = gain;
    }
}
