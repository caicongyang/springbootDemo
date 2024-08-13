package com.caicongyang.stock.controllers;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@Api(value = "index")
public class IndexController {

    @GetMapping(value = "hello")
    @ResponseBody
    public String hellow() {
        return "hello，Spring Boot ！";
    }





}
