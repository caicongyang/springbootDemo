package com.caicongyang.services;

public interface StockService {

    /**
     * 当天是否交易
     *
     * @return
     */
    Boolean TradeFlag();


    /**
     * 捕捉异常股票数据
     * *
     */
    void catchAbnormalStockData();


}
