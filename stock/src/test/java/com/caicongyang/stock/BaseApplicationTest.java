package com.caicongyang.stock;

import com.caicongyang.stock.domain.TStockMain;
import com.caicongyang.stock.service.impl.TStockMainServiceImpl;
import com.caicongyang.stock.utils.jacksonUtils;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@SpringBootTest(classes = Application.class)
public class BaseApplicationTest {


    @Autowired
    TStockMainServiceImpl tStockMainService;

    @Test
    public void test() throws Exception {


        String stocksInfo = tStockMainService.getStocksInfo("5b6a9ba2b2f272b721667f2f07c40ebd5bae4df5", "605500.XSHG");
        System.out.println(stocksInfo);

        if (stocksInfo.contains("error")) {
            stocksInfo = tStockMainService.getStocksInfo( tStockMainService.getJKToken(), "605500.XSHG");
        }


            //todo  token 过期判断
        List<String> jKIndustryStockList = Arrays.asList(stocksInfo.split("\n"));


        List<String> resultList = jKIndustryStockList.subList(1, jKIndustryStockList.size());
        TStockMain stockMain = tStockMainService.parseStockMain("605500.XSHG", resultList);
        System.out.println(jacksonUtils.jsonFromObject(stockMain));
    }



}
