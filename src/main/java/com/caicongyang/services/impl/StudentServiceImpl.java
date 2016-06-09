package com.caicongyang.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caicongyang.domain.Student;
import com.caicongyang.mapper.StudentMapper;
import com.caicongyang.services.StudentService;
import com.github.pagehelper.PageHelper;

@Service("studentService")
public class StudentServiceImpl implements StudentService {

	@Autowired
	StudentMapper studentMapper;

	@Override
	public List<Student> queryAll(Student stu) {
		if (stu.getPage() != null && stu.getRows() != null) {
			PageHelper.startPage(stu.getPage(), stu.getRows(), "id");
		}
		return studentMapper.selectAll();
	}

	@Override
	public Student getStudentById(Integer id) {
		return studentMapper.selectByPrimaryKey(id);
	}

	@Override
	public Integer setStudentById(Student stu) {
		return studentMapper.updateByPrimaryKeySelective(stu);
	}

}
