package com.caicongyang.lock.mapper;

import com.caicongyang.lock.LockerResource;
import java.util.Date;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author caicongyang
 */
public interface LockerMapper {

    /**
     *
     *
     * @param item
     * @return
     */
    @Select("select resource,description  from database_lock where resource = #{resource} ")
    LockerResource getResource(LockerResource item);


    @Insert("insert into database_lock (resource,description) values (#{resource},#{description}) ")
    Integer addResource(LockerResource item);


    @Delete("delete from  database_lock  where resource = #{resource}  ")
    Integer deleteResource(LockerResource item);


    @Delete("delete from  database_lock  where create_time < #{createTime}  ")
    Integer deleteUnuserResource(Date expireDate);
}
