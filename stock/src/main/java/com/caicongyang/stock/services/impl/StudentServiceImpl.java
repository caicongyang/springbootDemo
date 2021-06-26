package com.caicongyang.stock.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.stock.domain.Student;
import com.caicongyang.stock.mapper.StudentMapper;
import com.caicongyang.stock.services.StudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("studentService")
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private StudentMapper studentMapper;


    @Override
    public Student getStudentById(Integer id) {
        return studentMapper.selectById(id);
    }

    @Override
    public Integer setStudentById(Student stu) {
        return studentMapper.updateById(stu);
    }

}
