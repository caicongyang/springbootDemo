//package com.caicongyang.stock.component;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
////import com.caicongyang.lock.MysqlDistributedLock;
//import com.caicongyang.stock.BaseApplicationTest;
//import com.caicongyang.stock.domain.TStockMain;
//import com.caicongyang.stock.mapper.TStockMainMapper;
//import io.micrometer.core.instrument.util.JsonUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class LockerTest extends BaseApplicationTest {
//
//
//    @Autowired
//    TStockMainMapper mainMapper;
//
//
//    @Autowired
//    MysqlDistributedLock lock;
//
//
//    @Test
//    public void testLock() {
//        Boolean lock = this.lock.getLock("test", "test");
//
//        if (lock) {
//            TStockMain entity = mainMapper
//                    .selectOne(new QueryWrapper<TStockMain>().eq("stock_code", "000001.XSHE"));
//
//            System.out.println(entity);
//        } else {
//            System.out.println("请稍后重试");
//        }
//        this.lock.unLock("test");
//
//
//    }
//
//}
