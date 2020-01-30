package com.caicongyang.services.impl;

import com.caicongyang.mapper.CommonMapper;
import com.caicongyang.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockServiceImpl implements StockService {


    @Autowired
    CommonMapper mapper;


    @Override
    public Boolean TradeFlag() {
        return true;
    }

    @Override
    public List<Map<String, Object>> catchAbnormalStockData() {
        HashMap map = new HashMap();
        return mapper.queryBySql(map);
    }
}
