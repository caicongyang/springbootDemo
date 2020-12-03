package com.caicongyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caicongyang.domain.TStockMain;
import com.caicongyang.mapper.TStockMainMapper;
import com.caicongyang.service.ITStockMainService;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caicongyang
 * @since 2020-12-02
 */
@Service
public class TStockMainServiceImpl extends ServiceImpl<TStockMainMapper, TStockMain> implements
    ITStockMainService {

    @Resource
    TStockMainMapper tStockMainMapper;


    @Override
    @Cacheable(cacheNames = "TStockMainServiceImpl.getStockNameByStockCode" ,key = "#stockCode")
    public String getStockNameByStockCode(String stockCode) {
        if (StringUtils.isBlank(stockCode)) {
            return StringUtils.EMPTY;
        }
        TStockMain entity = tStockMainMapper.selectOne(new QueryWrapper<TStockMain>()
            .eq("stock_code", stockCode));
        return entity == null ? "" : entity.getStockName();
    }
}
