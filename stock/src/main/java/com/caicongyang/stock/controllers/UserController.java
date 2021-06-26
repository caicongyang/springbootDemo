package com.caicongyang.stock.controllers;

import com.caicongyang.stock.component.PrometheusCustomMonitor;
import com.caicongyang.stock.domain.Adress;
import com.caicongyang.stock.domain.UserInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author caicongyang
 * @version id: UserController, v 0.1 17/10/25 下午8:28 caicongyang1 Exp $$
 */
@RestController
@RequestMapping(value = "/user/v1")
public class UserController {

    private static Map<Long, UserInfo> uMap = new ConcurrentHashMap<>();

    @Autowired
    private PrometheusCustomMonitor monitor;

    static {
        UserInfo u1 = new UserInfo();
        u1.setUserId(1L);
        u1.setAge(11);
        u1.setName("jack");
        Adress adress = new Adress();
        adress.setCountry("China");
        adress.setProvince("Fujian");
        adress.setCity("Fuzhou");
        u1.setAdress(adress);
        uMap.put(u1.getUserId(),u1);
    }

    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", name = "Authorization", required = true, value = "token", dataType = "String", defaultValue = "service 123") })
    @ApiOperation(value = "新增学生", notes = "新增学生")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Boolean setStudentById(@RequestBody UserInfo userInfo) {
        uMap.put(userInfo.getUserId(), userInfo);

        if(userInfo.getUserId() == 1){
            //模拟
            monitor.getStudentAddCountError().increment();
        }
        monitor.getStudentAddCount().increment();

        return true;
    }

    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", name = "Authorization", required = true, value = "token", dataType = "String", defaultValue = "service 123") })
    @ApiOperation(value = "查询学生", notes = "查询学生")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public UserInfo getUserInfoById(@ApiParam("id") @RequestParam("id") Long id) {
        return uMap.get(id);
    }


    @ApiImplicitParams({ @ApiImplicitParam(paramType = "header", name = "Authorization", required = true, value = "token", dataType = "String", defaultValue = "service 123") })
    @ApiOperation(value = "查询学生", notes = "查询学生")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public UserInfo get(@ApiParam("id") @PathVariable("id") Integer id) {
        return uMap.get(id);
    }

}
