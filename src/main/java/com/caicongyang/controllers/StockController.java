package com.caicongyang.controllers;

import com.caicongyang.common.Result;
import com.caicongyang.services.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
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
    @ApiOperation(value = "查询当天的股票异动数据", notes = "查询当天的股票异动数据")
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
}
