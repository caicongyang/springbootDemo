package com.caicongyang.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caicongyang.stock.domain.TStockLimit;
import com.caicongyang.stock.domain.TStockLimitDTO;
import com.caicongyang.stock.domain.TransactionAndLimitStockDTO;

import java.io.IOException;
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

    public List<TransactionAndLimitStockDTO>  getTransactionStockStockAndLimit(String currentDate) throws Exception;
}
