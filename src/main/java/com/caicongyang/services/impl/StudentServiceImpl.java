package com.caicongyang.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.domain.Student;
import com.caicongyang.mapper.StudentMapper;
import com.caicongyang.services.StudentService;
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
