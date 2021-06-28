package com.caicongyang.stock.controllers;


import com.caicongyang.core.basic.Result;
import com.caicongyang.stock.domain.TEtfHigherDTO;
import com.caicongyang.stock.domain.TTransactionEtf;
import com.caicongyang.stock.domain.TTransactionEtfDTO;
import com.caicongyang.stock.services.ITEtfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.text.ParseException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    @Cacheable(value = "querySortEtfStockData", key = "#currentDate")
    public @ResponseBody
    Result<List<TTransactionEtfDTO>> querySortEtfStockData(@RequestParam(value = "currentDate") String currentDate) throws Exception {
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
    @Cacheable(value = "getTransactionEtfData", key = "#currentDate")
    public @ResponseBody
    Result<List<TTransactionEtfDTO>> getTransactionEtfData(@RequestParam(value = "currentDate") String currentDate) throws Exception {
        try {
            return Result.ok(etfService.getTransactionEtfData(currentDate));
        } catch (Exception e) {
            logger.error("查询当天的etf股票异动数据失败", e);
            return Result.fail(e);
        }
    }


    @GetMapping("/getHigherEtf")
    @ApiOperation(value = "计算当日新高的etf", notes = "计算当日新高的etf")
    @Cacheable(value = "getHigherEtf", key = "#currentDate")
    public Result<List<TEtfHigherDTO>> getHigherEtf(
        @RequestParam(required = true, value = "currentDate") String currentDate) {
        try {
            return Result.ok(etfService.getHigherEtf(currentDate));
        } catch (
            Exception e) {
            logger.error("计算当日新高的etf", e);
            return Result.fail(e);
        }
    }


    @GetMapping("/calculateHigherEtf")
    @ApiOperation(value = "计算当日新高的etf", notes = "计算当日新高的etf")
    public void calculateHigherStock(
        @RequestParam(required = false, value = "currentDate") String currentDate)
        throws ParseException {
        etfService.calculateHigherStock(currentDate);
    }


}
