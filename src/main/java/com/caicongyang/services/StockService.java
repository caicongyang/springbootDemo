package com.caicongyang.services;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface StockService {

    /**
     * a.当天是否交易
     *
     * @return
     */
    Boolean TradeFlag();


    /**
     * b. 2：15根据当天成交量  捕捉异常股票数据
     * *
     */
    List<Map<String, Object>> catchTransactionStockData(String currentDate) throws Exception;


    /**
     * c.抓取异常股票的版块分布和概念分布
     */


    /**
     * d.抓取当天的交易数据
     */

    /**
     *  d.19：00 根据当天成交量，捕捉异常股票数据
     */


    /**
     * f.发送版本或者概念版块具体的股票代码到邮箱
     *
     */


}
