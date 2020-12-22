package com.caicongyang.component;

import com.caicongyang.services.ITEtfService;
import com.caicongyang.services.ITStockService;
import com.caicongyang.services.StockService;
import com.caicongyang.utils.TomDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.aspectj.bridge.Version.getTime;

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
}
