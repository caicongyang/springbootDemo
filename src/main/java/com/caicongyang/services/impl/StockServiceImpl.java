package com.caicongyang.services.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caicongyang.component.HttpClientProvider;
import com.caicongyang.domain.TStock;
import com.caicongyang.domain.TTransactionStock;
import com.caicongyang.domain.TTransactionTock;
import com.caicongyang.mail.MailService;
import com.caicongyang.mapper.CommonMapper;
import com.caicongyang.mapper.TStockMapper;
import com.caicongyang.mapper.TTransactionStockMapper;
import com.caicongyang.services.StockService;
import com.caicongyang.utils.JsonUtils;
import com.caicongyang.utils.TomDateUtils;
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

    private static String apiUrl = "https://dataapi.joinquant.com/apis";


    private static List<String> senderList = Arrays.asList("1491318829@qq.com", "lmxiels@163.com", "wy545777485@163.com");

    @Autowired
    HttpClientProvider provider;

    @Resource
    protected CommonMapper mapper;


    @Resource
    protected TStockMapper tStockMapper;

    @Resource
    protected TTransactionStockMapper tTransactionStockMapper;


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
    public List<Map<String, Object>> catchTransactionStockData(String currentDate) throws Exception {


        String preTradingDate = mapper.queryPreTradingDate(currentDate);

        HashMap map = new HashMap();

        map.put("currentDate", currentDate);
        map.put("preDate", preTradingDate);
        List<Map<String, Object>> maps = mapper.queryTransactionStock(map);

        String jkToken = getJKToken();
        for (Map<String, Object> item : maps) {
            String jKIndustryStocks = getJKIndustryStocks(jkToken, (String) item.get("stock_code"));
            List<String> jKIndustryStockList = Arrays.asList(jKIndustryStocks.split("\n"));
            List<String>  resultList =  jKIndustryStockList.subList(1,jKIndustryStockList.size());
            jKIndustryStocks = JsonUtils.jsonFromObject(resultList);
            item.put("concept_str", jKIndustryStocks);

            TTransactionStock stock = new TTransactionStock();
            stock.setStockCode((String) item.get("stock_code"));
            stock.setConceptStr(jKIndustryStocks);
            stock.setLastDayCompare(((BigDecimal) item.get("last_day_compare")).doubleValue());
            stock.setMeanRatio(((BigDecimal) item.get("mean_ratio")).doubleValue());
            stock.setTradingDay(currentDate);
            tTransactionStockMapper.insert(stock);


        }


//        for (String mail : senderList) {
//            mailService.sendSimpleMail(mail, currentDate + "异动股票", JsonUtils.jsonFromObject(maps));
//        }

        return maps;
    }


    /**
     * 获取聚宽的token
     *
     * @return
     * @throws Exception
     */
    public String getJKToken() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("method", "get_token");
        params.put("mob", "13774598865");
        params.put("pwd", "123456");
        //todo 需要把token 缓存起来
        return provider.doPostWithApplicationJson(apiUrl, params);
    }

    /**
     * 获取某只股票的聚宽行业信息
     *
     * @throws Exception
     */
    public String getJKIndustryStocks(String token, String code) throws Exception {
        /**
         *  example:
         *                {
         *                     "method": "get_industry_stocks",
         *                         "token": "5b6a9ba7b0f572bb6c287e280ed",
         *                         "code": "HY007",
         *                         "date": "2016-03-29"
         *                 }
         */

        Map<String, String> params = new HashMap<>();
        params.put("method", "get_industry");
        params.put("token", token);
        params.put("code", code);
        params.put("date", TomDateUtils.getDayPatternCurrentDay());

        return provider.doPostWithApplicationJson(apiUrl, params);
    }
}
