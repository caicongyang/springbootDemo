package com.caicongyang.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.domain.BreakthroughPlatformStock;
import com.caicongyang.domain.HighestInPeriodResult;
import com.caicongyang.domain.TStock;
import com.caicongyang.domain.TStockHigher;
import com.caicongyang.domain.TStockHigherDTO;
import com.caicongyang.domain.TStockMain;
import com.caicongyang.mapper.CommonMapper;
import com.caicongyang.mapper.TStockHigherMapper;
import com.caicongyang.mapper.TStockMapper;
import com.caicongyang.service.ITStockMainService;
import com.caicongyang.services.ITStockService;
import com.caicongyang.utils.TomDateUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caicongyang
 * @since 2020-05-31
 */
@Service
public class TStockServiceImpl extends ServiceImpl<TStockMapper, TStock> implements ITStockService {


    @Resource
    protected CommonMapper mapper;

    @Resource
    private TStockMapper stockMapper;

    @Resource
    private TStockHigherMapper higherMapper;


    @Resource
    private ITStockMainService mainService;

    @Override
    public List<TStock> calculateHigherStock(String tradingDay) throws ParseException {

        TStock stock = new TStock();
        Date date = TomDateUtils.formateDayPattern2Date(tradingDay);
        stock.setTradingDay(TomDateUtils.date2LocalDate(date));
        QueryWrapper<TStock> groupByWrapper = new QueryWrapper<>();
        groupByWrapper.setEntity(stock);
        groupByWrapper.groupBy("stock_code");
        groupByWrapper.select("stock_code");
        List<TStock> tStocks = stockMapper.selectList(groupByWrapper);

        for (TStock item : tStocks) {
            String stockCode = item.getStockCode();
            TStock queryItem = new TStock();
            QueryWrapper<TStock> queryByWrapper = new QueryWrapper<>();
            queryItem.setStockCode(stockCode);
            queryByWrapper.setEntity(queryItem);
            queryByWrapper.orderByDesc("trading_day");
            List<TStock> itemList = stockMapper.selectList(queryByWrapper);

            HighestInPeriodResult result = getHighestInPeriodResult(itemList);
            if (null != result && result.getIntervalDays() > 30) {
                TStockHigher entity = new TStockHigher();
                entity.setIntervalDays(result.getIntervalDays());
                entity.setPreviousHighsDate(
                    TomDateUtils.date2LocalDate(result.getPreviousHighsDate()));
                entity.setStockCode(result.getStockCode());
                entity.setTradingDay(stock.getTradingDay());
                higherMapper.insert(entity);
            }

        }

        return null;
    }

    @Override
    public List<TStockHigherDTO> getHigherStock(String tradingDay)
        throws ParseException, IOException {
        QueryWrapper<TStockHigher> queryWrapper = new QueryWrapper<>();
        TStockHigher entity = new TStockHigher();
        Date date = TomDateUtils.formateDayPattern2Date(tradingDay);
        entity.setTradingDay(TomDateUtils.date2LocalDate(date));
        queryWrapper.setEntity(entity);
        queryWrapper.orderByAsc("interval_days");
        List<TStockHigher> result = higherMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(result)) {
            String lastTradingDate = mapper.queryLastTradingDate(tradingDay);
            date = TomDateUtils.formateDayPattern2Date(lastTradingDate);
            entity.setTradingDay(TomDateUtils.date2LocalDate(date));
            queryWrapper.setEntity(entity);
            result = higherMapper.selectList(queryWrapper);
        }

        //其他展示字段补充
        List<TStockHigherDTO> returnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            for (TStockHigher item : result) {
                TStockHigherDTO dto = new TStockHigherDTO();
                BeanUtils.copyProperties(item, dto);
                dto.setStockName(mainService.getStockNameByStockCode(item.getStockCode()));
                TStockMain industryEntity = mainService.getIndustryByStockCode(item.getStockCode());
                if (industryEntity != null) {
                    dto.setJqL2(industryEntity.getJqL2());
                    dto.setZjw(industryEntity.getZjw());
                    dto.setSwL3(industryEntity.getSwL3());
                }
                returnList.add(dto);
            }
        }

        return returnList;
    }

    @Override
    public List<BreakthroughPlatformStock> getBreakthroughPlatform(String currentDate)
        throws ParseException {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("currentDate", StringUtils.isNotBlank(currentDate) ? currentDate
            : TomDateUtils.getDayPatternCurrentDay());

        List<Map<String, Object>> queryResultList = mapper.getBreakthroughPlatform(queryMap);
        if (CollectionUtils.isEmpty(queryResultList)) {
            queryMap.put("currentDate", mapper.queryLastTradingDate(currentDate));
            queryResultList = mapper.getBreakthroughPlatform(queryMap);

        }
        List<BreakthroughPlatformStock> result = new ArrayList<>();

        for (Map<String, Object> map : queryResultList) {
            BreakthroughPlatformStock stock = new BreakthroughPlatformStock();
            stock.setStockCode((String) map.getOrDefault("stock_code", ""));
            stock.setIntervalDays((Integer) map.getOrDefault("interval_days", ""));

            stock.setLastDayCompare((Double) map.getOrDefault("last_day_compare", ""));
            stock.setMeanRatio((Double) map.getOrDefault("mean_ratio", ""));

            stock.setJqL2((String) map.getOrDefault("jq_l2", ""));
            stock.setSwL3((String) map.getOrDefault("sw_l3", ""));
            stock.setZjw((String) map.getOrDefault("zjw", ""));
            stock.setTradingDay(TomDateUtils.formateDayPattern2Date((String) map.getOrDefault("trading_day", "")));
            stock.setStockName(mainService.getStockNameByStockCode(stock.getStockCode()));

            result.add(stock);

        }

        return result;


    }


    //当前天数，多少天内最高， 当一个比当前高的天数；
    private HighestInPeriodResult getHighestInPeriodResult(List<TStock> list) {
        int intervalDays = 0;
        Date previousHighsDate = null;
        TStock currentStockData = list.get(0);
        if (null == currentStockData.getHigh()) {
            return null;
        }

        for (int i = 1; i < list.size(); i++) {
            //验证数据的完整性
            if (null == list.get(i).getHigh()) {
                break;
            }
            if (currentStockData.getHigh() >= list.get(i).getHigh()) {
                intervalDays++;
            } else {
                previousHighsDate = TomDateUtils.LocalDate2date(list.get(i).getTradingDay());
                //找到大于当前股权的日期跳出循环
                break;
            }

        }
        HighestInPeriodResult result = new HighestInPeriodResult();
        result.setIntervalDays(intervalDays);
        result.setPreviousHighsDate(previousHighsDate);
        result.setStockCode(list.get(0).getStockCode());

        intervalDays = 0;
        previousHighsDate = null;
        return result;
    }





}
