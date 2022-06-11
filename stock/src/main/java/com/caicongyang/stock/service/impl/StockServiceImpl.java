package com.caicongyang.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caicongyang.httper.HttpClientProvider;
import com.caicongyang.stock.domain.TStock;
import com.caicongyang.stock.domain.TStockMain;
import com.caicongyang.stock.domain.TTransactionStock;
import com.caicongyang.stock.domain.TTransactionStockDTO;
import com.caicongyang.stock.mail.MailService;
import com.caicongyang.stock.mapper.CommonMapper;
import com.caicongyang.stock.mapper.TStockMapper;
import com.caicongyang.stock.mapper.TTransactionStockMapper;
import com.caicongyang.stock.service.ITStockMainService;
import com.caicongyang.stock.service.StockService;
import com.caicongyang.stock.utils.TomDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

/**
 *
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);


    private static String apiUrl = "https://dataapi.joinquant.com/apis";


    private static List<String> senderList = Arrays.asList("1491318829@qq.com", "lmxiels@163.com", "wy545777485@163.com");

    @Autowired
    HttpClientProvider provider;
    @Resource
    protected CommonMapper commonMapper;


    @Resource
    protected TStockMapper tStockMapper;

    @Resource
    protected TTransactionStockMapper tTransactionStockMapper;


    @Autowired
    private ITStockMainService itStockMainService;


    @Autowired
    private MailService mailService;

    @Override
    public Boolean TradeFlag() {
        TStock stock = new TStock();
        stock.setTradingDay(LocalDate.now());
        Wrapper<TStock> wrapper = new QueryWrapper<>(stock);
        return CollectionUtils.isEmpty(tStockMapper.selectList(wrapper)) ? false : true;
    }

    @Override
    public List<Map<String, Object>> catchTransactionStockData(String currentDate) throws Exception {

        currentDate = commonMapper.queryTradingDate(currentDate);

        String preTradingDate = commonMapper.queryPreTradingDate(currentDate);

        HashMap map = new HashMap();

        map.put("currentDate", currentDate);
        map.put("preDate", preTradingDate);
        List<Map<String, Object>> maps = commonMapper.queryTransactionStock(map);

        for (Map<String, Object> item : maps) {
            String stockCode = (String) item.get("stock_code");
            TTransactionStock stock = new TTransactionStock();
            stock.setStockCode(stockCode);
            stock.setLastDayCompare(((BigDecimal) item.get("last_day_compare")).doubleValue());
            stock.setMeanRatio(((BigDecimal) item.get("mean_ratio")).doubleValue());
            stock.setTradingDay(currentDate);
            TStockMain main = itStockMainService
                    .getIndustryByStockCode(stockCode);
            if (null != main) {
                stock.setJqL2(main.getJqL2());
                stock.setSwL3(main.getSwL3());
                stock.setZjw(main.getZjw());
            }

            double v = getCurrentGain(currentDate, preTradingDate, stockCode);
            stock.setGain(v);
            try {
                tTransactionStockMapper.insert(stock);
            } catch (Exception e) {
                logger.error("插入异常股票错误", e);
            }

        }


//        for (String mail : senderList) {
//            mailService.sendSimpleMail(mail, currentDate + "异动股票", JsonUtils.jsonFromObject(maps));
//        }

        return maps;
    }

    public double getCurrentGain(String currentDate, String preTradingDate, String stockCode) throws ParseException {
        // 查询今日股价信息
        TStock currentStock = new TStock();
        currentStock.setTradingDay(TomDateUtils.date2LocalDate(TomDateUtils.formateDayPattern2Date(currentDate)));
        currentStock.setStockCode(stockCode);
        Wrapper<TStock> currentWrapper = new QueryWrapper<>(currentStock);
        currentStock = tStockMapper.selectOne(currentWrapper);


        // 查询上一个交易日股价信息
        TStock preDayStock = new TStock();
        preDayStock.setTradingDay(TomDateUtils.date2LocalDate(TomDateUtils.formateDayPattern2Date(preTradingDate)));
        preDayStock.setStockCode(stockCode);
        Wrapper<TStock> wrapper = new QueryWrapper<>(preDayStock);
        preDayStock = tStockMapper.selectOne(wrapper);


        if (Objects.nonNull(currentStock)  &&  Objects.nonNull(preDayStock) &&  Objects.nonNull(preDayStock.getClose()) && Objects.nonNull(currentStock.getClose())) {
            return (currentStock.getClose() - preDayStock.getClose()) / preDayStock.getClose();
        } else {
            return 0.0d;
        }
    }

    @Override
    public List<TTransactionStockDTO> getTransactionStockData(String currentDate) throws Exception {
        TTransactionStock stock = new TTransactionStock();
        stock.setTradingDay(currentDate);
        Wrapper<TTransactionStock> wrapper = new QueryWrapper<>(stock);
        ((QueryWrapper<TTransactionStock>) wrapper).orderByDesc("jq_l2", "sw_l3", "zjw");
        List<TTransactionStock> reuslt = tTransactionStockMapper.selectList(wrapper);

        //如果当天没有，则获取最近一个交易日
        if (CollectionUtils.isEmpty(reuslt)) {
            String lastTradingDate = commonMapper.queryLastTradingDate();
            stock.setTradingDay(lastTradingDate);
            ((QueryWrapper<TTransactionStock>) wrapper).setEntity(stock).orderByDesc("jq_l2", "sw_l3", "zjw");
            ;
            reuslt = tTransactionStockMapper.selectList(wrapper);
        }

        List<TTransactionStockDTO> returnList = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(reuslt)) {
            for (TTransactionStock item : reuslt) {
                TTransactionStockDTO dto = new TTransactionStockDTO();
                BeanUtils.copyProperties(item, dto);
                dto.setStockName(itStockMainService.getStockNameByStockCode(item.getStockCode()));
                returnList.add(dto);
            }
        }
        return returnList;
    }


    @Override
    public List<Map<String, Object>> getIntervalTransactionStockData(String startDate, String endDate) {
        HashMap<String, String> param = new HashMap<>();
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        return commonMapper.getIntervalTransactionStockData(param);
    }


}
