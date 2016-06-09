package com.caicongyang.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.caicongyang.domain.Student;
import com.caicongyang.services.StudentService;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/student")
@Api(value = "学生信息服务")
public class StudentController {
	@Autowired
	StudentService studentService;

	@RequestMapping(value = "/list.html", method = RequestMethod.GET)
	@ApiOperation(value = "根据id查询学生", notes = "根据id查询学生信息")
	public ModelAndView list(@ApiParam("开始页面") @RequestParam("startPage") Integer startPage,
			@ApiParam("页面大小") @RequestParam("pageSize") Integer pageSize) {
		Student stu = new Student();
		stu.setPage(startPage);
		stu.setRows(pageSize);
		List<Student> list = studentService.queryAll(stu);
		ModelAndView model = new ModelAndView("student/list");
		PageInfo<Student> page = new PageInfo<Student>(list);
		model.addObject("page", page);
		return model;
	}

	@RequestMapping(value = "/getList", method = RequestMethod.GET)
	public @ResponseBody PageInfo<Student> getList(@ApiParam("开始页面") @RequestParam("startPage") Integer startPage,
			@ApiParam("页面大小") @RequestParam("pageSize") Integer pageSize) {
		Student stu = new Student();
		stu.setPage(startPage);
		stu.setRows(pageSize);
		List<Student> list = studentService.queryAll(stu);
		PageInfo<Student> page = new PageInfo<Student>(list);
		return page;
	}

	@ApiOperation(value = "根据id查询学生", notes = "根据id查询学生信息")
	@RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
	public @ResponseBody Student getStudentById(@ApiParam("学生id") @PathVariable("id") Integer id) {
		Student stu = studentService.getStudentById(id);
		return stu;
	}

	@ApiOperation(value = "根据id查询学生", notes = "根据id查询学生信息")
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public @ResponseBody Integer setStudentById(@ApiParam("学生id") @RequestParam("id") Integer id,
			@ApiParam("学生年龄") @RequestParam("age") String age) {
		Student stu = new Student();
		stu.setAge(age);
		stu.setId(id);
		Integer code = studentService.setStudentById(stu);
		return code;
	}

}
