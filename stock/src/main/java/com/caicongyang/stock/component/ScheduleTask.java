package com.caicongyang.stock.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caicongyang.stock.domain.TStock;
import com.caicongyang.stock.mapper.CommonMapper;
import com.caicongyang.stock.service.*;
import com.caicongyang.stock.utils.TomDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ScheduleTask {


    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    @Autowired
    StockService stockService;


    @Autowired
    ITEtfService itEtfService;


    @Autowired
    private ITStockService itStockService;


    @Autowired
    ITStockMainService stockMainService;

    @Autowired
    ITStockLimitService limitService;

    @Resource
    CommonMapper commonMapper;


    /**
     * 每天18点执行一次
     */
//    @Scheduled(cron = "0 0 18 * * ?")
    public void task() throws Exception {
        logger.info("执行任务开始....");
        if (stockService.TradeFlag()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(new Date());
            stockService.catchTransactionStockData(currentDate);
            itEtfService.catchTransactionStockData(currentDate);

        } else {
            logger.info(TomDateUtils.getDayPatternCurrentDay() + "：未获取到交易数据");

        }
        logger.info("执行任务结束....");
    }


    /**
     * 每天18点 30执行一次
     */
//    @Scheduled(cron = "0 30 18 * * ?")
    public void task2() throws Exception {
        logger.info("执行任务开始....");
        if (stockService.TradeFlag()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(new Date());
            itStockService.calculateHigherStock(currentDate);


            itEtfService.calculateHigherStock(currentDate);


        } else {
            logger.info(TomDateUtils.getDayPatternCurrentDay() + "：未获取到交易数据");

        }
        logger.info("执行任务结束....");
    }


    /**
     * 每天19点 30分执行一次
     */
//    @Scheduled(cron = "0 30 19 * * ?")
    public void task3() throws Exception {
        logger.info("执行任务开始....");
        if (stockService.TradeFlag()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(new Date());
            limitService.catchAllDaliyLimitStock(currentDate);
        } else {
            logger.info(TomDateUtils.getDayPatternCurrentDay() + "：未获取到交易数据");
        }
        logger.info("执行任务结束....");
    }


    /**
     * 每周六中午执行一次
     * 0 0 19 ? * FRI
     */
//    @Scheduled(cron = "0 0 19 ? * FRI")
    public void task4() throws Exception {
        logger.info("执行任务开始....");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(new Date());
        itStockService.calculateHigherWeekStock(currentDate);
        logger.info("执行任务结束....");
    }


    /**
     * 每天23点执行一次
     */
//    @Scheduled(cron = "0 0 23 * * ?")
    public void makeUpMainData() throws Exception {
        logger.info("执行任务补充主数据开始....");
        String lastTradingDate = commonMapper.queryLastTradingDate();
        List<TStock> list = itStockService.list(new LambdaQueryWrapper<TStock>().eq(TStock::getTradingDay, TomDateUtils.date2LocalDate(TomDateUtils.formateDayPattern2Date(lastTradingDate))));
        for (TStock stock : list) {
            stockMainService.getIndustryByStockCode(stock.getStockCode());
        }

        logger.info("执行任务补充主数据....");
    }


}
