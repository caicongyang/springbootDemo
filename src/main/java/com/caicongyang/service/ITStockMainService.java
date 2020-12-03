package com.caicongyang.service;

import com.caicongyang.domain.TStockMain;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author caicongyang
 * @since 2020-12-02
 */
public interface ITStockMainService extends IService<TStockMain> {


    public String getStockNameByStockCode(String stockCode);

}
