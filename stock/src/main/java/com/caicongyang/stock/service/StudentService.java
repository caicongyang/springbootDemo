package com.caicongyang.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caicongyang.stock.domain.Student;

public interface StudentService extends IService<Student> {


    Student getStudentById(Integer id);

    Integer setStudentById(Student stu);
}
