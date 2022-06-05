package com.caicongyang.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caicongyang.stock.domain.TStockLimit;
import com.caicongyang.stock.domain.TStockLimitDTO;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author caicongyang
 * @since 2022-06-03
 */
public interface TStockLimitMapper extends BaseMapper<TStockLimit> {


   List<TStockLimitDTO> getIntervalLimitStockData(HashMap map);


   List<TStockLimitDTO> getLimitAndTransactionStockStock(HashMap map);

}
