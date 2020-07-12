package com.caicongyang.controllers;

import com.caicongyang.common.Result;
import com.caicongyang.domain.TTransactionCounterStock;
import com.caicongyang.domain.TTransactionStock;
import com.caicongyang.services.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stock")
@Api(value = "股票信息服务")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);


    @Autowired
    private StockService stockService;

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
    public @ResponseBody
    Result<List<TTransactionStock>> getTransactionStockData(@RequestParam(required = false, value = "currentDate") String currentDate) throws Exception {

        List<TTransactionStock> result = null;
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
    public @ResponseBody
    Result<List<TTransactionCounterStock>> getIntervalTransactionStockData(@RequestParam(required = false, value = "startDate") String startDate,
                                                                           @RequestParam(required = false, value = "endDate") String endDate) throws Exception {
        List<TTransactionCounterStock> result = new ArrayList<>();
        try {
            List<Map<String, Object>> queryResult = stockService.getIntervalTransactionStockData(startDate, endDate);
            if (CollectionUtils.isEmpty(queryResult)) {
                return Result.ok(null);
            } else {
                for (Map<String, Object> map : queryResult) {
                    TTransactionCounterStock stock = new TTransactionCounterStock();
                    stock.setCounter((Long) map.getOrDefault("counter", null));
                    stock.setStockCode((String) map.getOrDefault("stock_code", ""));
                    stock.setJqL2((String) map.getOrDefault("jq_l2", ""));
                    stock.setSwL3((String) map.getOrDefault("sw_l3", ""));
                    stock.setZjw((String) map.getOrDefault("zjw", ""));
                    stock.setTradingDay((String) map.getOrDefault("trading_day", ""));
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


}
