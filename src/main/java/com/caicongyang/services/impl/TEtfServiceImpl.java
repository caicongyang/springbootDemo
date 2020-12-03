package com.caicongyang.services.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.domain.TEtf;
import com.caicongyang.domain.TTransactionEtf;
import com.caicongyang.domain.TTransactionEtfDTO;
import com.caicongyang.mapper.CommonMapper;
import com.caicongyang.mapper.TEtfMapper;
import com.caicongyang.mapper.TTransactionEtfMapper;
import com.caicongyang.service.ITStockMainService;
import com.caicongyang.services.ITEtfService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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


    @Resource
    private ITStockMainService itStockMainService;



    @Override
    public List<TTransactionEtfDTO> querySortEtfStockData(String currentDate) {

        String preTradingDate = mapper.queryPreTradingDate(currentDate);
        List<TTransactionEtfDTO> resultList = new ArrayList<>();
        HashMap queryMap = new HashMap();
        queryMap.put("currentDate", currentDate);
        queryMap.put("preDate", preTradingDate);
        List<Map<String, Object>> maps = tEtfMapper.querySortEtfStockData(queryMap);
        if (CollectionUtils.isEmpty(maps)) {
            //如果当天没有，则获取最近一个交易日
            String lastTradingDate = mapper.queryLastTradingDate(currentDate);
            queryMap.put("currentDate", lastTradingDate);
            maps = tEtfMapper.querySortEtfStockData(queryMap);
        }

        for (Map map : maps) {
            TTransactionEtfDTO item = new TTransactionEtfDTO();
            item.setStockCode((String) map.getOrDefault("stock_code", ""));
            item.setLastDayCompare(((BigDecimal) map.getOrDefault("last_day_compare", "")).doubleValue());
            item.setTradingDay(currentDate);
            item.setStockName(itStockMainService.getStockNameByStockCode(item.getStockCode()));
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
                item.setTradingDay(currentDate);
                resultList.add(item);
                tTransactionEtfMapper.insert(item);
            }
        }
        return resultList;
    }

    @Override
    public List<TTransactionEtfDTO> getTransactionEtfData(String currentDate) {
        TTransactionEtf queryItem = new TTransactionEtf();
        queryItem.setTradingDay(currentDate);
        Wrapper<TTransactionEtf> wrapper = new QueryWrapper<>(queryItem);
        List<TTransactionEtf> result = tTransactionEtfMapper.selectList(wrapper);
        List<TTransactionEtfDTO> returnList = new ArrayList<>();

        if (CollectionUtils.isEmpty(result)) {
            //如果当天没有，则获取最近一个交易日
            String lastTradingDate = mapper.queryLastTradingDate(currentDate);
            queryItem.setTradingDay(lastTradingDate);
            ((QueryWrapper<TTransactionEtf>) wrapper).setEntity(queryItem);
            result = tTransactionEtfMapper.selectList(wrapper);
        }

        if (CollectionUtils.isNotEmpty(result)) {
            for (TTransactionEtf etf : result) {
                TTransactionEtfDTO dto = new TTransactionEtfDTO();
                BeanUtils.copyProperties(etf, dto);
                dto.setStockName(itStockMainService.getStockNameByStockCode(etf.getStockCode()));
                returnList.add(dto);
            }
        }
        return returnList;
    }


}
