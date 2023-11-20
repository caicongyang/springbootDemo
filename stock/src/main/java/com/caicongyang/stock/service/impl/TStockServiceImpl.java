package com.caicongyang.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.stock.domain.*;
import com.caicongyang.stock.mapper.*;
import com.caicongyang.stock.service.ITStockMainService;
import com.caicongyang.stock.service.ITStockWeekHigherService;
import com.caicongyang.stock.service.ITStockService;
import com.caicongyang.stock.service.StockService;
import com.caicongyang.stock.utils.TomDateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caicongyang
 * @since 2020-05-31
 */
@Service
public class TStockServiceImpl extends ServiceImpl<TStockMapper, TStock> implements ITStockService {


    @Resource
    private ITStockMainService mainService;

    @Resource
    private ITStockWeekHigherService weekHigherService;


    @Resource
    private StockService stockService;


    @Resource
    protected CommonMapper mapper;

    @Resource
    private TStockMapper stockMapper;

    @Resource
    private TStockHigherMapper higherMapper;


    @Resource
    private TStockWeekHigherMapper weekHigherMapper;

    @Resource
    private TStockWeekMapper weekMapper;


    @Override
    public List<TStock> calculateHigherStock(String tradingDay) throws ParseException {

        TStock stock = new TStock();
        Date date = TomDateUtil.formateDayPattern2Date(tradingDay);
        stock.setTradingDay(TomDateUtil.date2LocalDate(date));
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

            String preTradingDate = mapper.queryPreTradingDate(tradingDay);

            HighestInPeriodResult result = getHighestInPeriodResult(itemList);
            if (null != result && result.getIntervalDays() > 30) {
                TStockHigher entity = new TStockHigher();
                entity.setIntervalDays(result.getIntervalDays());
                entity.setPreviousHighsDate(
                        TomDateUtil.date2LocalDate(result.getPreviousHighsDate()));
                entity.setStockCode(result.getStockCode());
                entity.setTradingDay(stock.getTradingDay());


                double currentGain = stockService.getCurrentGain(tradingDay, preTradingDate, result.getStockCode());

                entity.setGain(currentGain);
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
        Date date = TomDateUtil.formateDayPattern2Date(tradingDay);
        entity.setTradingDay(TomDateUtil.date2LocalDate(date));
        queryWrapper.setEntity(entity);
        queryWrapper.orderByAsc("interval_days");
        List<TStockHigher> result = higherMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(result)) {
            String lastTradingDate = mapper.queryLastTradingDate();
            date = TomDateUtil.formateDayPattern2Date(lastTradingDate);
            entity.setTradingDay(TomDateUtil.date2LocalDate(date));
            queryWrapper.setEntity(entity);
            result = higherMapper.selectList(queryWrapper);
        }

        //其他展示字段补充
        List<TStockHigherDTO> returnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            for (TStockHigher item : result) {
                TStockHigherDTO dto = new TStockHigherDTO();
                BeanUtils.copyProperties(item, dto);
                TStockMain industryEntity = mainService.getIndustryByStockCode(item.getStockCode());
                if (industryEntity != null) {
                    dto.setJqL2(industryEntity.getJqL2());
                    dto.setZjw(industryEntity.getZjw());
                    dto.setSwL3(industryEntity.getSwL3());
                    dto.setStockName(industryEntity.getStockName());
                }
                returnList.add(dto);
            }
        }

        //java8 联合排序

        Comparator<TStockHigherDTO> byJqL2 = Comparator.nullsLast(Comparator
                .comparing(TStockHigherDTO::getJqL2, Comparator.nullsLast(Comparator.naturalOrder())));

        Comparator<TStockHigherDTO> bySwL3 = Comparator.nullsLast(Comparator
                .comparing(TStockHigherDTO::getSwL3, Comparator.nullsLast(Comparator.naturalOrder())));

        Comparator<TStockHigherDTO> byZjw = Comparator.nullsLast(Comparator
                .comparing(TStockHigherDTO::getZjw, Comparator.nullsLast(Comparator.naturalOrder())));

        // 联合排序
        Comparator<TStockHigherDTO> finalComparator = Comparator
                .nullsLast(byJqL2.thenComparing(bySwL3).thenComparing(byZjw));

        return returnList.stream().sorted(finalComparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<BreakthroughPlatformStock> getBreakthroughPlatform(String currentDate)
            throws ParseException, IOException {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("currentDate", StringUtils.isNotBlank(currentDate) ? currentDate
                : TomDateUtil.getDayPatternCurrentDay());

        List<Map<String, Object>> queryResultList = mapper.getBreakthroughPlatform(queryMap);
        if (CollectionUtils.isEmpty(queryResultList)) {
            queryMap.put("currentDate", mapper.queryLastTradingDate());
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
            stock.setGain((Double) map.getOrDefault("gain",0d));

            stock.setTradingDay(TomDateUtil.formateDayPattern2Date((String) map.getOrDefault("trading_day", "")));
            stock.setStockName(mainService.getStockNameByStockCode(stock.getStockCode()));

            result.add(stock);

        }

        //java8 联合排序

        Comparator<BreakthroughPlatformStock> byJqL2 = Comparator
                .comparing(BreakthroughPlatformStock::getJqL2, Comparator.nullsLast(Comparator.naturalOrder()));

        Comparator<BreakthroughPlatformStock> bySwL3 = Comparator
                .comparing(BreakthroughPlatformStock::getSwL3, Comparator.nullsLast(Comparator.naturalOrder()));

        Comparator<BreakthroughPlatformStock> byZjw = Comparator
                .comparing(BreakthroughPlatformStock::getZjw, Comparator.nullsLast(Comparator.naturalOrder()));

        // 联合排序
        Comparator<BreakthroughPlatformStock> finalComparator = Comparator
                .nullsLast(byJqL2.thenComparing(bySwL3).thenComparing(byZjw));

        return result.stream().sorted(finalComparator)
                .collect(Collectors.toList());


    }

    @Override
    public List<VolumeGtYesterdayStockDTO> getVolumeGtYesterdayStock(String currentDate)
            throws IOException {

        String day = mapper.queryLastTradingDate();

        String preTradingDate = mapper.queryPreTradingDate(day);
        HashMap queryMap = new HashMap();
        queryMap.put("currentDate", day);
        queryMap.put("preDate", preTradingDate);
        List<Map<String, Object>> list = mapper.getVolumeGtYesterdayStock(queryMap);
        List<VolumeGtYesterdayStockDTO> resultList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(list)) {
            for (Map<String, Object> map : list) {
                VolumeGtYesterdayStockDTO dto = new VolumeGtYesterdayStockDTO();
                dto.setCounter(((BigDecimal) map.get("counter")).doubleValue());
                dto.setStockCode((String) map.get("stock_code"));
                dto.setTradingDay(day);
                TStockMain stockMain = mainService.getIndustryByStockCode(dto.getStockCode());
                if (null != stockMain) {
                    dto.setStockName(stockMain.getStockName());
                    dto.setJqL2(stockMain.getJqL2());
                    dto.setZjw(stockMain.getZjw());
                    dto.setSwL3(stockMain.getSwL3());
                }
                resultList.add(dto);

            }
        }

        //java8 联合排序

        Comparator<VolumeGtYesterdayStockDTO> byJqL2 = Comparator
                .comparing(VolumeGtYesterdayStockDTO::getJqL2, Comparator.nullsLast(Comparator.naturalOrder()));

        Comparator<VolumeGtYesterdayStockDTO> bySwL3 = Comparator
                .comparing(VolumeGtYesterdayStockDTO::getSwL3, Comparator.nullsLast(Comparator.naturalOrder()));

        Comparator<VolumeGtYesterdayStockDTO> byZjw = Comparator
                .comparing(VolumeGtYesterdayStockDTO::getZjw, Comparator.nullsLast(Comparator.naturalOrder()));

        // 联合排序
        Comparator<VolumeGtYesterdayStockDTO> finalComparator = Comparator
                .nullsLast(byJqL2.thenComparing(bySwL3).thenComparing(byZjw));

        return resultList.stream().sorted(finalComparator)
                .collect(Collectors.toList());
    }

    @Override
    public void calculateHigherWeekStock(String tradingDay) throws ParseException {

        TStockWeek stock = new TStockWeek();
        Date date = TomDateUtil.formateDayPattern2Date(tradingDay);
        stock.setTradingDay(TomDateUtil.date2LocalDate(date));
        QueryWrapper<TStockWeek> groupByWrapper = new QueryWrapper<>();
        groupByWrapper.setEntity(stock);
        groupByWrapper.groupBy("stock_code");
        groupByWrapper.select("stock_code");
        List<TStockWeek> tStocks = weekMapper.selectList(groupByWrapper);

        for (TStockWeek item : tStocks) {
            String stockCode = item.getStockCode();
            TStockWeek queryItem = new TStockWeek();
            QueryWrapper<TStockWeek> queryByWrapper = new QueryWrapper<>();
            queryItem.setStockCode(stockCode);
            queryByWrapper.setEntity(queryItem);
            queryByWrapper.orderByDesc("trading_day");
            List<TStockWeek> itemList = weekMapper.selectList(queryByWrapper);

            HighestInPeriodResult result = getHighestWeekInPeriodResult(itemList);
            if (null != result && result.getIntervalDays() > 30) {
                TStockWeekHigher entity = new TStockWeekHigher();
                entity.setIntervalDays(result.getIntervalDays());
                entity.setPreviousHighsDate(
                        TomDateUtil.date2LocalDate(result.getPreviousHighsDate()));
                entity.setStockCode(result.getStockCode());
                entity.setTradingDay(stock.getTradingDay());
                weekHigherMapper.insert(entity);
            }

        }
    }

    @Override
    public List<TStockHigherDTO> getHigherWeekStock(String tradingDay)
            throws ParseException, IOException {
        QueryWrapper<TStockWeekHigher> queryWrapper = new QueryWrapper<>();
        TStockWeekHigher entity = new TStockWeekHigher();
        Date date = TomDateUtil.formateDayPattern2Date(tradingDay);
        entity.setTradingDay(TomDateUtil.date2LocalDate(date));
        queryWrapper.setEntity(entity);
        queryWrapper.orderByAsc("interval_days");
        List<TStockWeekHigher> result = weekHigherMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(result)) {
            String lastTradingDate = mapper.queryLastWeekTradingDate();
            date = TomDateUtil.formateDayPattern2Date(lastTradingDate);
            entity.setTradingDay(TomDateUtil.date2LocalDate(date));
            queryWrapper.setEntity(entity);
            result = weekHigherMapper.selectList(queryWrapper);
        }

        //其他展示字段补充
        List<TStockHigherDTO> returnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            for (TStockWeekHigher item : result) {
                TStockHigherDTO dto = new TStockHigherDTO();
                BeanUtils.copyProperties(item, dto);
                TStockMain industryEntity = mainService.getIndustryByStockCode(item.getStockCode());
                if (industryEntity != null) {
                    dto.setJqL2(industryEntity.getJqL2());
                    dto.setZjw(industryEntity.getZjw());
                    dto.setSwL3(industryEntity.getSwL3());
                    dto.setStockName(industryEntity.getStockName());
                }
                returnList.add(dto);
            }
        }

        //java8 联合排序

        Comparator<TStockHigherDTO> byJqL2 = Comparator.nullsLast(Comparator
                .comparing(TStockHigherDTO::getJqL2, Comparator.nullsLast(Comparator.naturalOrder())));

        Comparator<TStockHigherDTO> bySwL3 = Comparator.nullsLast(Comparator
                .comparing(TStockHigherDTO::getSwL3, Comparator.nullsLast(Comparator.naturalOrder())));

        Comparator<TStockHigherDTO> byZjw = Comparator.nullsLast(Comparator
                .comparing(TStockHigherDTO::getZjw, Comparator.nullsLast(Comparator.naturalOrder())));

        // 联合排序
        Comparator<TStockHigherDTO> finalComparator = Comparator
                .nullsLast(byJqL2.thenComparing(bySwL3).thenComparing(byZjw));

        return returnList.stream().sorted(finalComparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<TTransactionStockDTO> querySortWeekStockData(String currentDate) throws IOException {
        String lastTradingDate = currentDate;
        String preTradingDate = mapper.queryPreWeekTradingDate(lastTradingDate);
        List<TTransactionStockDTO> resultList = new ArrayList<>();
        HashMap queryMap = new HashMap();
        queryMap.put("currentDate", lastTradingDate);
        queryMap.put("preDate", preTradingDate);
        List<Map<String, Object>> maps = weekMapper.querySortWeekStockData(queryMap);
        if (CollectionUtils.isEmpty(maps)) {

            lastTradingDate = mapper.queryLastWeekTradingDate();
            preTradingDate = mapper.queryPreWeekTradingDate(lastTradingDate);
            queryMap.put("currentDate", lastTradingDate);
            queryMap.put("preDate", preTradingDate);
            maps = weekMapper.querySortWeekStockData(queryMap);
        }

        for (Map map : maps) {
            TTransactionStockDTO item = new TTransactionStockDTO();
            item.setStockCode((String) map.getOrDefault("stock_code", ""));
            item.setLastDayCompare(
                    ((BigDecimal) map.getOrDefault("last_day_compare", "")).doubleValue());
            item.setTradingDay(currentDate);

            TStockMain industryEntity = mainService.getIndustryByStockCode(item.getStockCode());
            if (industryEntity != null) {
                item.setJqL2(industryEntity.getJqL2());
                item.setZjw(industryEntity.getZjw());
                item.setSwL3(industryEntity.getSwL3());
                item.setStockName(industryEntity.getStockName());
            }


            resultList.add(item);
        }


        //java8 联合排序

        Comparator<TTransactionStockDTO> byJqL2 = Comparator.nullsLast(Comparator
                .comparing(TTransactionStockDTO::getJqL2, Comparator.nullsLast(Comparator.naturalOrder())));

        Comparator<TTransactionStockDTO> bySwL3 = Comparator.nullsLast(Comparator
                .comparing(TTransactionStockDTO::getSwL3, Comparator.nullsLast(Comparator.naturalOrder())));

        Comparator<TTransactionStockDTO> byZjw = Comparator.nullsLast(Comparator
                .comparing(TTransactionStockDTO::getZjw, Comparator.nullsLast(Comparator.naturalOrder())));

        // 联合排序
        Comparator<TTransactionStockDTO> finalComparator = Comparator
                .nullsLast(byJqL2.thenComparing(bySwL3).thenComparing(byZjw));

        return resultList.stream().sorted(finalComparator)
                .collect(Collectors.toList());

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
                previousHighsDate = TomDateUtil.LocalDate2date(list.get(i).getTradingDay());
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


    //当前天数，多少天内最高， 当一个比当前高的天数；
    private HighestInPeriodResult getHighestWeekInPeriodResult(List<TStockWeek> list) {
        int intervalDays = 0;
        Date previousHighsDate = null;
        TStockWeek currentStockData = list.get(0);
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
                previousHighsDate = TomDateUtil.LocalDate2date(list.get(i).getTradingDay());
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
