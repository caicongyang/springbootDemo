package com.caicongyang.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.httper.HttpClientProvider;
import com.caicongyang.stock.domain.TStockMain;
import com.caicongyang.stock.mapper.TStockMainMapper;
import com.caicongyang.stock.service.ITStockMainService;
import com.caicongyang.stock.utils.TomDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caicongyang
 * @since 2020-12-02
 */
@Service
public class TStockMainServiceImpl extends ServiceImpl<TStockMainMapper, TStockMain> implements
        ITStockMainService {

    private static final Logger logger = LoggerFactory.getLogger(TStockMainServiceImpl.class);


    @Resource
    TStockMainMapper tStockMainMapper;

    @Autowired
    HttpClientProvider provider;

    private static String apiUrl = "https://dataapi.joinquant.com/apis";

    private static ConcurrentHashMap<String, String> tokenMap = new ConcurrentHashMap<>();


    static {
        tokenMap.put("token", "5b6a9ba2b2f272b721667f2f07cb00b2313216c1");
    }


    @Override
    @Cacheable(cacheNames = "getStockNameByStockCode", key = "#stockCode")
    public String getStockNameByStockCode(String stockCode) throws IOException {
        if (StringUtils.isBlank(stockCode)) {
            return StringUtils.EMPTY;
        }
        TStockMain entity = tStockMainMapper.selectOne(new QueryWrapper<TStockMain>()
                .eq("stock_code", stockCode));
        if (entity != null && StringUtils.isNoneBlank(entity.getStockName())) {
            return entity.getStockName();
        }
        return StringUtils.EMPTY;
    }


    @Override
    @Cacheable(cacheNames = "getIndustryByStockCode", key = "#stockCode")
    public TStockMain getIndustryByStockCode(String stockCode) throws IOException {
        TStockMain entity = tStockMainMapper
                .selectOne(new QueryWrapper<TStockMain>().eq("stock_code", stockCode));
        if (null != entity && !StringUtils.isBlank(entity.getJqL2()) && !StringUtils
                .isBlank(entity.getSwL3())
                && !StringUtils.isBlank(entity.getZjw())) {
            return entity;
        } else {
            TStockMain tStockMain = _getIndustryByStockCode(tokenMap.get("token"), stockCode);
            if (null != tStockMain) {
                if (null != entity) {
                    tStockMainMapper
                            .update(tStockMain,
                                    new LambdaUpdateWrapper<TStockMain>().eq(TStockMain::getStockCode, stockCode));
                } else {
                    tStockMainMapper.insert(tStockMain);
                }
            }
            return tStockMain;
        }
    }

    public TStockMain _getIndustryByStockCode(String jkToken, String stockCode) throws IOException {
        String jkIndustryStr = getJKIndustryStocks(jkToken, stockCode);
        if (jkIndustryStr.contains("error")) {
            tokenMap.put("token", getJKToken());
            jkIndustryStr = getJKIndustryStocks(tokenMap.get("token"), stockCode);

        }

        //todo  token 过期判断
        List<String> jKIndustryStockList = Arrays.asList(jkIndustryStr.split("\n"));
        List<String> resultList = jKIndustryStockList.subList(1, jKIndustryStockList.size());
        return parseIndustryStockData(stockCode, resultList);
    }


    public String getJKToken() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("method", "get_token");
        params.put("mob", "13774598865");
        params.put("pwd", "123456");
        //todo 需要把token 缓存起来
        return provider.doPostWithApplicationJson(apiUrl, params);
    }


    /**
     * 获取某只股票的聚宽行业信息
     */
    public String getJKIndustryStocks(String token, String code) throws IOException {
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


    /**
     * 获取某只股票的聚宽行业信息
     */
    public String getStocksInfo(String token, String code) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("method", "get_security_info");
        params.put("token", token);
        params.put("code", code);
        params.put("date", TomDateUtils.getDayPatternCurrentDay());

        return provider.doPostWithApplicationJson(apiUrl, params);
    }


    private TStockMain parseIndustryStockData(String stockCode, List<String> resultList) {
        TStockMain stock = new TStockMain();
        stock.setStockCode(stockCode);
        for (String industry : resultList) {
            if (industry.indexOf("jq_l2") >= 0) {
                stock.setJqL2(industry.split(",")[2]);
            }

            if (industry.indexOf("sw_l3") >= 0) {
                stock.setSwL3(industry.split(",")[2]);

            }

            if (industry.indexOf("zjw") >= 0) {
                stock.setZjw(industry.split(",")[2]);
            }
        }
        return stock;
    }


    public TStockMain parseStockMain(String stockCode, List<String> resultList) {
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        TStockMain stockMain = new TStockMain();
        stockMain.setStockName(resultList.get(0).split(",")[1]);
        stockMain.setStockCode(stockCode);
        stockMain.setType(resultList.get(0).split(",")[5]);
        return stockMain;

    }

}
