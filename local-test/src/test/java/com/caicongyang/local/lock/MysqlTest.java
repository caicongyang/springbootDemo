package com.caicongyang.local.lock;

import com.caicongyang.local.BaseApplicationTest;
import com.caicongyang.lock.MysqlDistributedLock;
import com.zaxxer.hikari.HikariDataSource;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MysqlTest extends BaseApplicationTest {


    @Resource
    MysqlDistributedLock lock;


    @Autowired
    private DataSource dataSource;


    @Autowired()
    @Qualifier("lockerDataSource")
    private DataSource lockerDataSource;


    @Test
    public void springDataSourceTest() {

        System.out.println(dataSource instanceof HikariDataSource);

        System.out.println(lockerDataSource instanceof HikariDataSource);




    }


    @Test
    public void test1() throws InterruptedException {
        lock.getLock("test1", "");
        Thread.sleep(1000L);
        lock.unLock("test1");
    }


    @Test
    public void test2() throws InterruptedException {
        lock.getLock("test1", "");
        lock.getLock("test1", "");
    }

}
