package com.caicongyang.component;

import com.caicongyang.services.StockService;
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

    /**
     * 每天17点执行一次
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void task() throws Exception {
        logger.info("执行任务开始....");
        if (stockService.TradeFlag()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(new Date());
            stockService.catchTransactionStockData(currentDate);
        }
        logger.info("执行任务结束....");
    }

}
