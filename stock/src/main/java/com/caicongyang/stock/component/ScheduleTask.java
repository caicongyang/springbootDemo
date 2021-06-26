package com.caicongyang.stock.component;

import com.caicongyang.stock.services.ITEtfService;
import com.caicongyang.stock.services.ITStockService;
import com.caicongyang.stock.services.StockService;
import com.caicongyang.stock.utils.TomDateUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {


    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    @Autowired
    StockService stockService;


    @Autowired
    ITEtfService itEtfService;


    @Autowired
    private ITStockService itStockService;




    /**
     * 每天18点执行一次
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void task() throws Exception {
        logger.info("执行任务开始....");
        if (stockService.TradeFlag()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(new Date());
            stockService.catchTransactionStockData(currentDate);

            itEtfService.catchTransactionStockData(currentDate);

        }else{
            logger.info(TomDateUtils.getDayPatternCurrentDay()+"：未获取到交易数据");

        }
        logger.info("执行任务结束....");
    }



    /**
     * 每天19点执行一次
     */
    @Scheduled(cron = "0 30 18 * * ?")
    public void task2() throws Exception {
        logger.info("执行任务开始....");
        if (stockService.TradeFlag()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(new Date());
            itStockService.calculateHigherStock(currentDate);


        }else{
            logger.info(TomDateUtils.getDayPatternCurrentDay()+"：未获取到交易数据");

        }
        logger.info("执行任务结束....");
    }


    /**
     * 每天19点执行一次
     */
    @Scheduled(cron = "0 0 19 * * ?")
    public void task3() throws Exception {
        logger.info("执行任务开始....");
        if (stockService.TradeFlag()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(new Date());
            itEtfService.calculateHigherStock(currentDate);
        }else{
            logger.info(TomDateUtils.getDayPatternCurrentDay()+"：未获取到交易数据");
        }
        logger.info("执行任务结束....");
    }


    /**
     * 每周六中午执行一次
     * 0 0 12 ? * WED
     */
    @Scheduled(cron = "0 0 19 ? * FRI")
    public void task4() throws Exception {
        logger.info("执行任务开始....");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(new Date());
        itStockService.calculateHigherWeekStock(currentDate);
        logger.info("执行任务结束....");
    }
}
