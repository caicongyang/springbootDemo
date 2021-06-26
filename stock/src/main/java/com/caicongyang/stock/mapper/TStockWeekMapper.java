package com.caicongyang.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caicongyang.stock.domain.TStockWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author caicongyang
 * @since 2021-02-17
 */
public interface TStockWeekMapper extends BaseMapper<TStockWeek> {


    List<Map<String, Object>> querySortWeekStockData(HashMap map);


}
