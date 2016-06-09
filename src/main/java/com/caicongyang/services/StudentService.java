package com.caicongyang.services;

import java.util.List;

import com.caicongyang.domain.Student;

public interface StudentService {

	List<Student> queryAll(Student stu);

	Student getStudentById(Integer id);
	
	Integer setStudentById(Student stu); 
}
