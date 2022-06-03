package com.caicongyang.stock.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class BreakthroughPlatformStock {

    /**
     * 前期高点与当前天数间隔
     */
    private Integer intervalDays;


    /**
     * 当前时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date tradingDay;


    private String stockCode;

    private String stockName;

    private Double lastDayCompare;

    private Double meanRatio;


    @ApiModelProperty(value = "申万行业")
    private String swL3;

    @ApiModelProperty(value = "聚宽行业")
    private String jqL2;

    @ApiModelProperty(value = "证监会行业")
    private String zjw;

    @ApiModelProperty(value = "当天涨幅")
    private Double gain;

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Date getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(Date tradingDay) {
        this.tradingDay = tradingDay;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
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


    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }


    public Double getGain() {
        return gain;
    }

    public void setGain(Double gain) {
        this.gain = gain;
    }
}
