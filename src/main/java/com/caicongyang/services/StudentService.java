package com.caicongyang.services;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caicongyang.domain.Student;

public interface StudentService extends IService<Student> {


    Student getStudentById(Integer id);

    Integer setStudentById(Student stu);
}
