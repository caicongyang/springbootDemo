package com.caicongyang.services.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caicongyang.domain.TEtf;
import com.caicongyang.domain.TTransactionEtf;
import com.caicongyang.domain.TTransactionStock;
import com.caicongyang.mapper.CommonMapper;
import com.caicongyang.mapper.TEtfMapper;
import com.caicongyang.mapper.TTransactionEtfMapper;
import com.caicongyang.services.ITEtfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caicongyang
 * @since 2020-07-08
 */
@Service
public class TEtfServiceImpl extends ServiceImpl<TEtfMapper, TEtf> implements ITEtfService {

    @Resource
    protected CommonMapper mapper;


    @Resource
    protected TEtfMapper tEtfMapper;

    @Resource
    protected TTransactionEtfMapper tTransactionEtfMapper;


    @Override
    public List<TTransactionEtf> querySortEtfStockData(String currentDate) {

        String preTradingDate = mapper.queryPreTradingDate(currentDate);
        List<TTransactionEtf> resultList = new ArrayList<>();
        HashMap queryMap = new HashMap();
        queryMap.put("currentDate", currentDate);
        queryMap.put("preDate", preTradingDate);
        List<Map<String, Object>> maps = tEtfMapper.querySortEtfStockData(queryMap);
        for (Map map : maps) {
            TTransactionEtf item = new TTransactionEtf();
            item.setStockCode((String) map.getOrDefault("stock_code", ""));
            item.setLastDayCompare(((BigDecimal) map.getOrDefault("last_day_compare", "")).doubleValue());
            resultList.add(item);
        }
        return resultList;
    }

    @Override
    public List<TTransactionEtf> catchTransactionStockData(String currentDate) {
        String preTradingDate = mapper.queryPreTradingDate(currentDate);
        List<TTransactionEtf> resultList = new ArrayList<>();
        HashMap queryMap = new HashMap();
        queryMap.put("currentDate", currentDate);
        queryMap.put("preDate", preTradingDate);
        List<Map<String, Object>> maps = tEtfMapper.catchTransactionEtf(queryMap);
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map map : maps) {
                TTransactionEtf item = new TTransactionEtf();
                item.setStockCode((String) map.getOrDefault("stock_code", ""));
                item.setLastDayCompare(((BigDecimal) map.getOrDefault("last_day_compare", "")).doubleValue());
                item.setMeanRatio(((BigDecimal) map.get("mean_ratio")).doubleValue());
                resultList.add(item);
                tTransactionEtfMapper.insert(item);
            }
        }
        return resultList;
    }

    @Override
    public List<TTransactionEtf> getTransactionEtfData(String currentDate) {
        TTransactionEtf queryItem = new TTransactionEtf();
        queryItem.setTradingDay(currentDate);
        Wrapper<TTransactionEtf> wrapper = new QueryWrapper<>(queryItem);
        List<TTransactionEtf> result = tTransactionEtfMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        } else {
            //如果当天没有，则获取最近一个交易日
            String lastTradingDate = mapper.queryLastTradingDate(currentDate);
            queryItem.setTradingDay(lastTradingDate);
            ((QueryWrapper<TTransactionEtf>) wrapper).setEntity(queryItem);
            result = tTransactionEtfMapper.selectList(wrapper);
        }
        return result;
    }


}
