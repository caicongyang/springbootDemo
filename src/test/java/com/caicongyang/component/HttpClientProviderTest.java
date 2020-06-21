package com.caicongyang.component;

import com.caicongyang.BaseApplicationTest;
import com.caicongyang.utils.JsonUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientProviderTest extends BaseApplicationTest {

    @Autowired
    HttpClientProvider provider;


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
        Map<String, String> params = new HashMap<>();
        params.put("method", "get_industry");
        params.put("token", "5b6a9ba2b2f272b721667f2c0ecf0eb9d722d45d");
        params.put("code", "000011.XSHE");
        params.put("date", "2020-06-12");

        String s = JsonUtils.jsonFromObject(params);
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


}
