package com.caicongyang.service;

import com.caicongyang.domain.TStockMain;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author caicongyang
 * @since 2020-12-02
 */
public interface ITStockMainService extends IService<TStockMain> {


    public String getStockNameByStockCode(String stockCode) throws IOException;

    public TStockMain getIndustryByStockCode(String stockCode) throws IOException;

}
