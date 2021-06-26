package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("student")
public class Student implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7322378264763045290L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String age;


}
