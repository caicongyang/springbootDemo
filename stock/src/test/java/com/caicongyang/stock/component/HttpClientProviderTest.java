package com.caicongyang.stock.component;

import com.caicongyang.httper.HttpClientProvider;
import com.caicongyang.stock.BaseApplicationTest;
import com.caicongyang.stock.domain.TStockMain;
import com.caicongyang.stock.service.ITStockMainService;
import com.caicongyang.stock.utils.jacksonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HttpClientProviderTest extends BaseApplicationTest {

    @Autowired
    HttpClientProvider provider;

    @Autowired
    ITStockMainService itStockMainService;


    private String apiUrl = "https://dataapi.joinquant.com/apis";

    @Test
    public void test() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("method", "get_token");
        params.put("mob", "13774598865");
        params.put("pwd", "123456");

        String result = provider.doPostWithApplicationJson(apiUrl, params);
        System.out.println(result);
    }


    @Test
    public void test1() throws Exception {
//        {
//            "method": "get_industry_stocks",
//                "token": "5b6a9ba7b0f572bb6c287e280ed",
//                "code": "HY007",
//                "date": "2016-03-29"
//        }

        String token2=  "5b6a9ba2b2f272b721667f2f07cb00b2313216c1";

        Map<String, String> params = new HashMap<>();
        params.put("method", "get_industry");
        params.put("token", token2);
        params.put("code", "000011.XSHE");
        params.put("date", "2020-06-12");

        String s = jacksonUtils.jsonFromObject(params);
        System.out.println(s);

        String result = provider.doPostWithApplicationJson(apiUrl, params);
        System.out.println(result);

        System.out.println("------------------");
        List<String> resultList = Arrays.asList(result.split("\n"));


        System.out.println("------------------");

        for (String industry : resultList) {
            if (industry.indexOf("jq_l2") >= 0 || industry.indexOf("sw_l3") >= 0 || industry.indexOf("zjw") >= 0) {
                System.out.println(industry.split(",")[2]);
            }
        }


    }




    @Test
    public void test2() throws Exception{
        String token2=  "5b6a9ba2b2f272b721667f2f07cb00b92430e94f";

        Map<String, String> params = new HashMap<>();
        params.put("method", "get_all_securities");
        params.put("token", token2);
        params.put("code", "etf");
        params.put("date", "2020-12-02");

        String s = jacksonUtils.jsonFromObject(params);
        System.out.println(s);

        String result = provider.doPostWithApplicationJson(apiUrl, params);
        System.out.println(result);


        List<String> resultList = Arrays.asList(result.split("\n"));

        List<TStockMain> insertlist = new ArrayList<>();

       // resultList.remove(0);

        for(String key : resultList){

            TStockMain entity = new TStockMain();
            String[] keys = key.split(",");
            entity.setStockCode(keys[0]);
            entity.setStockName(keys[1]);
            entity.setType(keys[5]);
            insertlist.add(entity);
        }


        itStockMainService.saveBatch(insertlist);



    }


}
