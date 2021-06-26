package com.caicongyang.stock.controllers;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caicongyang.stock.domain.Student;
import com.caicongyang.stock.services.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/student")
@Api(value = "学生信息服务")
public class StudentController {
    @Autowired
    StudentService studentService;

    @RequestMapping(value = "/list.html", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询学生", notes = "根据id查询学生信息")
    public ModelAndView list(@ApiParam("开始页面") @RequestParam("startPage") Integer startPage,
                             @ApiParam("页面大小") @RequestParam("pageSize") Integer pageSize,
                             @ApiParam("请求入参数") @RequestBody Student stu) {
        IPage<Student> page = new Page<>();
        page.setCurrent(1);
        page.setSize(5);
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.setEntity(stu);
        IPage<Student> reustl = studentService.page(page, wrapper);
        ModelAndView model = new ModelAndView();
        model.addObject("page", reustl);
        return model;
    }

    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public @ResponseBody
    IPage<Student> getList(@ApiParam("开始页面") @RequestParam("startPage") Integer startPage,
                           @ApiParam("页面大小") @RequestParam("pageSize") Integer pageSize,
                           @ApiParam("请求入参数") @RequestBody Student stu) {
        IPage<Student> page = new Page<>();
        page.setCurrent(1);
        page.setSize(5);
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.setEntity(stu);
        return studentService.page(page, wrapper);

    }

    @ApiOperation(value = "根据id查询学生", notes = "根据id查询学生信息")
    @RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Student getStudentById(@ApiParam("学生id") @PathVariable("id") Integer id) {
        Student stu = studentService.getStudentById(id);
        return stu;
    }

    @ApiOperation(value = "根据id查询学生", notes = "根据id查询学生信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Integer setStudentById(@ApiParam("学生id") @RequestParam("id") Integer id,
                           @ApiParam("学生年龄") @RequestParam("age") String age) {
        Student stu = new Student();
        stu.setAge(age);
        stu.setId(id);
        Integer code = studentService.setStudentById(stu);
        return code;
    }

}
