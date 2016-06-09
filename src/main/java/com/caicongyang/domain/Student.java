package com.caicongyang.domain;

import java.io.Serializable;

public class Student extends BaseDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7322378264763045290L;
	private String name;
	private String age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

}
