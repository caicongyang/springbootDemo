package com.caicongyang.lock.domain;

import java.io.Serializable;
import java.util.Date;


/**
 * @author caicongyang
 */
public class LockerResource implements Serializable {

    private static final long serialVersionUID = 3802955039541851463L;
    private String resource;

    private String description;

    private Date createTime;

    private Date updateTime;


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
