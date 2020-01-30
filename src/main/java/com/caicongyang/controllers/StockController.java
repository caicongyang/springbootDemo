package com.caicongyang.controllers;

import com.caicongyang.services.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stock")
@Api(value = "股票信息服务")
public class StockController {


    @Autowired
    private StockService stockService;

    @GetMapping("/catchAbnormalStockData")
    @ApiOperation(value = "查询当天的股票异常数据", notes = "查询当天的股票异常数据")
    public @ResponseBody List<Map<String, Object>> catchAbnormalStockData() {
        return stockService.catchAbnormalStockData();
    }
}
