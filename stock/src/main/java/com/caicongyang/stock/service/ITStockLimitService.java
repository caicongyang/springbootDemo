package com.caicongyang.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caicongyang.stock.domain.TStockLimit;
import com.caicongyang.stock.domain.TStockLimitDTO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author caicongyang
 * @since 2022-06-03
 */
public interface ITStockLimitService extends IService<TStockLimit> {

    public void catchAllDaliyLimitStock(String tradingDay) throws Exception;

    public List<TStockLimitDTO> getIntervalLimitStockData(String startDate, String endDate) throws Exception;

    public List<TStockLimitDTO> getLimitAndTransactionStockStock(String currentDate) throws Exception;
}
