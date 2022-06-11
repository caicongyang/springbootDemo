package com.caicongyang.stock.mapper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MP 支持不需要 UserMapper.xml 这个模块演示内置 CRUD
 *
 * @author zhanghai
 * @email 80730305@yonghui.cn
 * @date 2018-12-17 22:15:03
 */
public interface CommonMapper {


    List<Map<String, Object>> queryTransactionStock(HashMap map);


    String queryPreTradingDate(String currentDate);


    String  queryTradingDate(String currentDate);


    String queryLastTradingDate();


    String queryLastWeekTradingDate();


    String queryPreWeekTradingDate(String currentDate);


    List<Map<String, Object>> getIntervalTransactionStockData(HashMap<String, String> map);


    List<Map<String, Object>> getBreakthroughPlatform(HashMap<String, String> map);


    List<Map<String, Object>> getVolumeGtYesterdayStock(HashMap<String, String> map);

}

