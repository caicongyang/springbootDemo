package com.caicongyang.services;

import com.caicongyang.domain.TEtf;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caicongyang.domain.TTransactionEtf;

import com.caicongyang.domain.TTransactionEtfDTO;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author caicongyang
 * @since 2020-07-08
 */
public interface ITEtfService extends IService<TEtf> {

    List<TTransactionEtfDTO> querySortEtfStockData(String currentDate);

    List<TTransactionEtf> catchTransactionStockData(String currentDate);

    List<TTransactionEtfDTO> getTransactionEtfData(String currentDate);
}
