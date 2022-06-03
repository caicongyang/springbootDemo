package com.caicongyang.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.stock.domain.TStock;
import com.caicongyang.stock.domain.TStockLimit;
import com.caicongyang.stock.domain.TStockLimitDTO;
import com.caicongyang.stock.domain.TStockMain;
import com.caicongyang.stock.mapper.CommonMapper;
import com.caicongyang.stock.mapper.TStockLimitMapper;
import com.caicongyang.stock.service.ITStockMainService;
import com.caicongyang.stock.service.ITStockLimitService;
import com.caicongyang.stock.service.ITStockService;
import com.caicongyang.stock.service.StockService;
import com.caicongyang.stock.utils.TomDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caicongyang
 * @since 2022-06-03
 */
@Service
public class TStockLimitServiceImpl extends ServiceImpl<TStockLimitMapper, TStockLimit> implements ITStockLimitService {

    @Resource
    private ITStockService tStockService;

    @Resource
    StockService stockService;

    @Resource
    CommonMapper commonMapper;

    @Resource
    ITStockMainService itStockMainService;


    public void catchAllDaliyLimitStock(String tradingDay) throws Exception {
        String preTradingDate = commonMapper.queryPreTradingDate(tradingDay);
        Date date = TomDateUtils.formateDayPattern2Date(tradingDay);
        LocalDate localDate = TomDateUtils.date2LocalDate(date);

        List<TStock> list = tStockService.list(new LambdaQueryWrapper<TStock>().eq(TStock::getTradingDay, localDate));
        List<TStockLimit> insertList = new ArrayList<>();
        for (TStock stock : list) {
            double currentGain = stockService.getCurrentGain(tradingDay, preTradingDate, stock.getStockCode());
            if (currentGain >= 0.095) {
                TStockLimit limit = new TStockLimit();
                limit.setStockCode(stock.getStockCode());
                limit.setGain(currentGain);
                limit.setTradingDay(localDate);
                insertList.add(limit);
            }
        }
        this.saveBatch(insertList);
    }


    @Override
    public List<TStockLimitDTO> getIntervalLimitStockData(String startDate, String endDate) throws Exception {


        LocalDate startLocalDate = TomDateUtils.date2LocalDate(TomDateUtils.formateDayPattern2Date(startDate));
        LocalDate endLocalDate = TomDateUtils.date2LocalDate(TomDateUtils.formateDayPattern2Date(endDate));

        List<TStockLimit> limitList = this.list(new LambdaQueryWrapper<TStockLimit>().between(true, TStockLimit::getTradingDay, startLocalDate, endLocalDate));

        if (CollectionUtils.isEmpty(limitList)) {
            return null;
        }


        List<String> collect = limitList.stream().map(a -> a.getStockCode()).collect(Collectors.toList());
        List<TStockMain> stockMainList = itStockMainService.list(new LambdaQueryWrapper<TStockMain>().in(true, TStockMain::getStockCode, collect));
        Map<String, List<TStockMain>> stockCodeMap = stockMainList.stream().collect(Collectors.groupingBy(e -> e.getStockCode()));
        List<TStockLimitDTO> reList = new LinkedList<>();


        for (TStockLimit item : limitList) {
            TStockLimitDTO dto = new TStockLimitDTO();
            List<TStockMain> tStockMains = stockCodeMap.get(item.getStockCode());
            TStockMain tStockMain = tStockMains.get(0);
            BeanUtils.copyProperties(tStockMain, dto);
            BeanUtils.copyProperties(item, dto);
            reList.add(dto);
        }

        return reList;
    }
}
