package com.caicongyang.stock.domain;

import com.google.gson.annotations.SerializedName;

public class OneHourTicker {


    /**
     * {
     * "e": "1hTicker",    // Event type
     * "E": 123456789,     // Event time
     * "s": "BNBBTC",      // Symbol
     * "p": "0.0015",      // Price change
     * "P": "250.00",      // Price change percent
     * "o": "0.0010",      // Open price
     * "h": "0.0025",      // High price
     * "l": "0.0010",      // Low price
     * "c": "0.0025",      // Last price
     * "w": "0.0018",      // Weighted average price
     * "v": "10000",       // Total traded base asset volume
     * "q": "18",          // Total traded quote asset volume
     * "O": 0,             // Statistics open time
     * "C": 86400000,      // Statistics close time
     * "F": 0,             // First trade ID
     * "L": 18150,         // Last trade Id
     * "n": 18151          // Total number of trades
     * }
     */


    @SerializedName("e")
    private String event;

    @SerializedName("E")
    private long eventTime;

    @SerializedName("s")
    private String symbol;

    @SerializedName("p")
    private String priceChange;

    @SerializedName("P")
    private String priceChangePercent;


    @SerializedName("o")
    private String openPrice;

    @SerializedName("h")
    private String highPrice;

    @SerializedName("l")
    private String lowPrice;

    @SerializedName("c")
    private String lastPrice;

    @SerializedName("w")
    private String weightedAveragePrice;


    @SerializedName("v")
    private String baseAssetVolume;

    @SerializedName("q")
    private String quoteAssetVolume;

    @SerializedName("O")
    private Long openTime;

    @SerializedName("C")
    private Long closeTime;

    @SerializedName("F")
    private Long FirstTradeID;

    @SerializedName("L")
    private Long lastTradeId;

    @SerializedName("n")
    private Integer totalNumberOftrades;


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }

    public String getPriceChangePercent() {
        return priceChangePercent;
    }

    public void setPriceChangePercent(String priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getWeightedAveragePrice() {
        return weightedAveragePrice;
    }

    public void setWeightedAveragePrice(String weightedAveragePrice) {
        this.weightedAveragePrice = weightedAveragePrice;
    }

    public String getBaseAssetVolume() {
        return baseAssetVolume;
    }

    public void setBaseAssetVolume(String baseAssetVolume) {
        this.baseAssetVolume = baseAssetVolume;
    }

    public String getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public void setQuoteAssetVolume(String quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public Long getFirstTradeID() {
        return FirstTradeID;
    }

    public void setFirstTradeID(Long firstTradeID) {
        FirstTradeID = firstTradeID;
    }

    public Long getLastTradeId() {
        return lastTradeId;
    }

    public void setLastTradeId(Long lastTradeId) {
        this.lastTradeId = lastTradeId;
    }

    public Integer getTotalNumberOftrades() {
        return totalNumberOftrades;
    }

    public void setTotalNumberOftrades(Integer totalNumberOftrades) {
        this.totalNumberOftrades = totalNumberOftrades;
    }
}
