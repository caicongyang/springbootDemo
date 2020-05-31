package com.caicongyang.services.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caicongyang.domain.TStock;
import com.caicongyang.mail.MailService;
import com.caicongyang.mapper.CommonMapper;
import com.caicongyang.mapper.TStockMapper;
import com.caicongyang.services.StockService;
import com.caicongyang.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class StockServiceImpl implements StockService {


    @Resource
    protected CommonMapper mapper;


    @Resource
    protected TStockMapper tStockMapper;


    @Autowired
    MailService mailService;

    @Override
    public Boolean TradeFlag() {
        TStock stock = new TStock();
        stock.setTradingDay(LocalDate.now());
        Wrapper<TStock> wrapper = new QueryWrapper<>(stock);
        return CollectionUtils.isEmpty(tStockMapper.selectList(wrapper)) ? false : true;
    }

    @Override
    public List<Map<String, Object>> catchTransactionStockData(String currentDate) throws ParseException {

        String preTradingDate = mapper.queryPreTradingDate();

        HashMap map = new HashMap();

        map.put("currentDate", currentDate);
        map.put("preDate", preTradingDate);
        List<Map<String, Object>> maps = mapper.queryTransactionStock(map);

        mailService.sendSimpleMail("1491318829@qq.com", currentDate + "异动股票", JsonUtils.jsonFromObject(maps));

        return maps;
    }
}
