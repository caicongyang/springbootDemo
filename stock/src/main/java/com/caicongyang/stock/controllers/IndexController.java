package com.caicongyang.stock.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping(value = "hello")
    @ResponseBody
    public String hellow() {
        return "hello，Spring Boot ！";
    }





}
