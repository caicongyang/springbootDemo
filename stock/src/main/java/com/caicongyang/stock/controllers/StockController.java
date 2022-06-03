package com.caicongyang.stock.controllers;

import com.caicongyang.core.basic.Result;
import com.caicongyang.stock.domain.BreakthroughPlatformStock;
import com.caicongyang.stock.domain.TStockHigherDTO;
import com.caicongyang.stock.domain.TTransactionCounterStockDTO;
import com.caicongyang.stock.domain.TTransactionStockDTO;
import com.caicongyang.stock.domain.VolumeGtYesterdayStockDTO;
import com.caicongyang.stock.service.ITStockMainService;
import com.caicongyang.stock.service.ITStockService;
import com.caicongyang.stock.service.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/stock")
@Api(value = "股票信息服务")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);


    @Autowired
    private StockService stockService;


    @Autowired
    private ITStockService itStockService;

    @Resource
    private ITStockMainService itStockMainService;


    @GetMapping("/catchTransactionStockData")
    @ApiOperation(value = "捕获当天的股票异动数据", notes = "查询当天的股票异动数据")
    public @ResponseBody
    Result<List<Map<String, Object>>> catchTransactionStockData(@RequestParam(value = "currentDate") String currentDate) throws Exception {
        List<Map<String, Object>> result = null;
        try {
            result = stockService.catchTransactionStockData(currentDate);
            return Result.ok(result);
        } catch (ParseException e) {
            logger.error("查询当天的股票异动数据失败", e);
            e.printStackTrace();
            return Result.fail(e);
        }
    }


    @GetMapping("/getTransactionStockData")
    @ApiOperation(value = "查询当天的股票异动数据", notes = "查询当天的股票异动数据")
    @Cacheable(value = "getTransactionStockData", key = "#currentDate")
    public @ResponseBody
    Result<List<TTransactionStockDTO>> getTransactionStockData(
        @RequestParam(required = false, value = "currentDate") String currentDate)
        throws Exception {

        List<TTransactionStockDTO> result = null;
        try {
            result = stockService.getTransactionStockData(currentDate);
            return Result.ok(result);
        } catch (ParseException e) {
            logger.error("查询当天的股票异动数据失败", e);
            e.printStackTrace();
            return Result.fail(e);
        }
    }


    @GetMapping("/getIntervalTransactionStockData")
    @ApiOperation(value = "查询时间间隔的股票异动数据", notes = "查询时间间隔内的股票异动数据")
    @Cacheable(value = "getIntervalTransactionStockData", key = "#startDate+ '_' +#endDate")
    public @ResponseBody
    Result<List<TTransactionCounterStockDTO>> getIntervalTransactionStockData(
        @RequestParam(required = false, value = "startDate") String startDate,
                                                                           @RequestParam(required = false, value = "endDate") String endDate) throws Exception {
        List<TTransactionCounterStockDTO> result = new ArrayList<>();
        try {
            List<Map<String, Object>> queryResult = stockService.getIntervalTransactionStockData(startDate, endDate);
            if (CollectionUtils.isEmpty(queryResult)) {
                return Result.ok(null);
            } else {
                for (Map<String, Object> map : queryResult) {
                    TTransactionCounterStockDTO stock = new TTransactionCounterStockDTO();
                    stock.setCounter((Long) map.getOrDefault("counter", null));
                    stock.setStockCode((String) map.getOrDefault("stock_code", ""));
                    stock.setJqL2((String) map.getOrDefault("jq_l2", ""));
                    stock.setSwL3((String) map.getOrDefault("sw_l3", ""));
                    stock.setZjw((String) map.getOrDefault("zjw", ""));
                    stock.setTradingDay((String) map.getOrDefault("trading_day", ""));
                    stock.setStockName(
                        itStockMainService.getStockNameByStockCode(stock.getStockCode()));
                    stock.setGain((Double) map.getOrDefault("gain", 0d));
                    result.add(stock);
                }
                return Result.ok(result);
            }
        } catch (Exception e) {
            logger.error("查询当天的股票异动数据失败", e);
            e.printStackTrace();
            return Result.fail(e);
        }
    }


    @GetMapping("/calculateHigherStock")
    @ApiOperation(value = "计算当日新高的股票", notes = "查询当日新高的股票")
    public void calculateHigherStock(
        @RequestParam(required = false, value = "tradingDay") String tradingDay)
        throws ParseException {
        itStockService.calculateHigherStock(tradingDay);
    }


    @GetMapping("/getHigherStock")
    @ApiOperation(value = "获取当日新高的股票", notes = "获取当日新高的股票")
    @Cacheable(value = "getHigherStock", key = "#tradingDay")
    public @ResponseBody
    Result<List<TStockHigherDTO>> getHigherStock(
        @RequestParam(required = true, value = "tradingDay") String tradingDay)
        throws ParseException, IOException {
        return Result.ok(itStockService.getHigherStock(tradingDay));
    }


    @GetMapping("/get-breakthrough-platform")
    @ApiOperation(value = "获取平台突破的股票", notes = "获取平台突破的股票")
    @Cacheable(value = "getBreakthroughPlatform", key = "#currentDate")
    public @ResponseBody
    Result<List<BreakthroughPlatformStock>> getBreakthroughPlatform(
        @RequestParam(required = true, value = "currentDate") String currentDate)
        throws ParseException, IOException {
        return Result.ok(itStockService.getBreakthroughPlatform(currentDate));
    }


    @GetMapping("/getVolumeGtYesterdayStock")
    @ApiOperation(value = "当天12点获取交易量大于昨天的股票")
    @Cacheable(value = "getVolumeGtYesterdayStock", key = "#currentDate")
    public @ResponseBody
    Result<List<VolumeGtYesterdayStockDTO>> getVolumeGtYesterdayStock(
        @RequestParam(required = true, value = "currentDate") String currentDate)
        throws IOException {
        return Result.ok(itStockService.getVolumeGtYesterdayStock(currentDate));
    }


    @GetMapping("/calculateHigherWeekStock")
    @ApiOperation(value = "计算每周新高的股票", notes = "计算每周新高的股票")
    public void calculateHigherWeekStock(
        @RequestParam(required = false, value = "tradingDay") String tradingDay)
        throws ParseException {
        itStockService.calculateHigherWeekStock(tradingDay);
    }


    @GetMapping("/getHigherWeekStock")
    @ApiOperation(value = "获取每周新高的股票", notes = "获取每周新高的股票")
    @Cacheable(value = "getHigherWeekStock", key = "#tradingDay")
    public @ResponseBody
    Result<List<TStockHigherDTO>> getHigherWeekStock(
        @RequestParam(required = true, value = "tradingDay") String tradingDay)
        throws ParseException, IOException {
        return Result.ok(itStockService.getHigherWeekStock(tradingDay));
    }


    @GetMapping("/querySortWeekStockData")
    @ApiOperation(value = "按周成交额与前一个成交额的比率倒序排名")
    @Cacheable(value = "querySortWeekStockData", key = "#currentDate")
    public @ResponseBody
    Result<List<TTransactionStockDTO>> querySortWeekStockData(@RequestParam(value = "currentDate") String currentDate) throws Exception {
        try {
            return Result.ok(itStockService.querySortWeekStockData(currentDate));
        } catch (Exception e) {
            logger.error("按周成交额与前一个成交额的比率倒序排名数据失败", e);
            return Result.fail(e);
        }
    }
}
