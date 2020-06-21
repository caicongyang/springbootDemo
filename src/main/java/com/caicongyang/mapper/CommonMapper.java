package com.caicongyang.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
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
@Mapper
public interface CommonMapper {


    List<Map<String, Object>> queryTransactionStock(HashMap map);


    String queryPreTradingDate(String currentDate);


    String queryLastTradingDate(String currentDate);

}

