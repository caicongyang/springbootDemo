package com.caicongyang.stock.domain;

import com.google.gson.annotations.SerializedName;

public class KlineTicker {

    /**
     * {
     *   "e": "kline",     // 事件类型
     *   "E": 123456789,   // 事件时间
     *   "s": "BNBBTC",    // 交易对
     *   "k": {
     *     "t": 123400000, // 这根K线的起始时间
     *     "T": 123460000, // 这根K线的结束时间
     *     "s": "BNBBTC",  // 交易对
     *     "i": "1m",      // K线间隔
     *     "f": 100,       // 这根K线期间第一笔成交ID
     *     "L": 200,       // 这根K线期间末一笔成交ID
     *     "o": "0.0010",  // 这根K线期间第一笔成交价
     *     "c": "0.0020",  // 这根K线期间末一笔成交价
     *     "h": "0.0025",  // 这根K线期间最高成交价
     *     "l": "0.0015",  // 这根K线期间最低成交价
     *     "v": "1000",    // 这根K线期间成交量
     *     "n": 100,       // 这根K线期间成交笔数
     *     "x": false,     // 这根K线是否完结(是否已经开始下一根K线)
     *     "q": "1.0000",  // 这根K线期间成交额
     *     "V": "500",     // 主动买入的成交量
     *     "Q": "0.500",   // 主动买入的成交额
     *     "B": "123456"   // 忽略此参数
     *   }
     * }
     */

    /**
     * {
     *   "e": "kline",     // Event type
     *   "E": 123456789,   // Event time
     *   "s": "BNBBTC",    // Symbol
     *   "k": {
     *     "t": 123400000, // Kline start time
     *     "T": 123460000, // Kline close time
     *     "s": "BNBBTC",  // Symbol
     *     "i": "1m",      // Interval
     *     "f": 100,       // First trade ID
     *     "L": 200,       // Last trade ID
     *     "o": "0.0010",  // Open price
     *     "c": "0.0020",  // Close price
     *     "h": "0.0025",  // High price
     *     "l": "0.0015",  // Low price
     *     "v": "1000",    // Base asset volume
     *     "n": 100,       // Number of trades
     *     "x": false,     // Is this kline closed?
     *     "q": "1.0000",  // Quote asset volume
     *     "V": "500",     // Taker buy base asset volume
     *     "Q": "0.500",   // Taker buy quote asset volume
     *     "B": "123456"   // Ignore
     *   }
     * }
     */


    @SerializedName("t")
    private String startTime;

    @SerializedName("T")
    private long closeTime;

    @SerializedName("s")
    private String symbol;

    @SerializedName("i")
    private String interval;

    @SerializedName("f")
    private String firstTradeId;

    @SerializedName("L")
    private String lastTradeId;

    @SerializedName("o")
    private String openPrice;

    @SerializedName("c")
    private String closePrice;

    @SerializedName("h")
    private String highPrice;

    @SerializedName("l")
    private String lowPrice;


    @SerializedName("v")
    private String baseAssetVolume;

    @SerializedName("n")
    private String numberOfTrades;

    @SerializedName("x")
    private String isklineClosed;


    @SerializedName("q")
    private String quoteAssetVolume;

    @SerializedName("V")
    private Double takerBuyBaseAssetVolume;

    @SerializedName("Q")
    private Double takerBuyQuoteAssetVolume;



    @SerializedName("B")
    private Long ignore;


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getFirstTradeId() {
        return firstTradeId;
    }

    public void setFirstTradeId(String firstTradeId) {
        this.firstTradeId = firstTradeId;
    }

    public String getLastTradeId() {
        return lastTradeId;
    }

    public void setLastTradeId(String lastTradeId) {
        this.lastTradeId = lastTradeId;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
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

    public String getBaseAssetVolume() {
        return baseAssetVolume;
    }

    public void setBaseAssetVolume(String baseAssetVolume) {
        this.baseAssetVolume = baseAssetVolume;
    }

    public String getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(String numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public String getIsklineClosed() {
        return isklineClosed;
    }

    public void setIsklineClosed(String isklineClosed) {
        this.isklineClosed = isklineClosed;
    }

    public String getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public void setQuoteAssetVolume(String quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }


    public Double getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }

    public void setTakerBuyBaseAssetVolume(Double takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }

    public Double getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }

    public void setTakerBuyQuoteAssetVolume(Double takerBuyQuoteAssetVolume) {
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }

    public Long getIgnore() {
        return ignore;
    }

    public void setIgnore(Long ignore) {
        this.ignore = ignore;
    }
}
