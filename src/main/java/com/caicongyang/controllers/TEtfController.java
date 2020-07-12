package com.caicongyang.controllers;


import com.caicongyang.common.Result;
import com.caicongyang.domain.TTransactionEtf;
import com.caicongyang.services.ITEtfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author caicongyang
 * @since 2020-07-08
 */
@Api(value = "etf")
@RestController
@RequestMapping("/t-etf")
public class TEtfController {


    private static final Logger logger = LoggerFactory.getLogger(TEtfController.class);


    @Autowired
    ITEtfService etfService;


    @GetMapping("/querySortEtfStockData")
    @ApiOperation(value = "大于1000w的etf按当日成交额与前一个成交额的比率倒序排名", notes = "查询当天的股票异动数据")
    public @ResponseBody
    Result<List<TTransactionEtf>> querySortEtfStockData(@RequestParam(value = "currentDate") String currentDate) throws Exception {
        try {
            return Result.ok(etfService.querySortEtfStockData(currentDate));
        } catch (Exception e) {
            logger.error("查询当天的etf股票异动数据失败", e);
            e.printStackTrace();
            return Result.fail(e);
        }
    }


    @GetMapping("/catchTransactionEtfData")
    @ApiOperation(value = "捕获当天的etf异动数据", notes = "查询当天的股票异动数据")
    public @ResponseBody
    Result<List<TTransactionEtf>> catchTransactionEtfData(@RequestParam(value = "currentDate") String currentDate) throws Exception {
        try {
            return Result.ok(etfService.catchTransactionStockData(currentDate));
        } catch (Exception e) {
            logger.error("查询当天的etf股票异动数据失败", e);
            e.printStackTrace();
            return Result.fail(e);
        }
    }


    @GetMapping("/getTransactionEtfData")
    @ApiOperation(value = "捕获当天的etf异动数据", notes = "查询当天的股票异动数据")
    public @ResponseBody
    Result<List<TTransactionEtf>> getTransactionEtfData(@RequestParam(value = "currentDate") String currentDate) throws Exception {
        try {
            return Result.ok(etfService.getTransactionEtfData(currentDate));
        } catch (Exception e) {
            logger.error("查询当天的etf股票异动数据失败", e);
            e.printStackTrace();
            return Result.fail(e);
        }
    }


}
