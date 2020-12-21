package com.caicongyang.services;

import com.caicongyang.common.Result;
import com.caicongyang.domain.TEtf;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caicongyang.domain.TEtfHigher;
import com.caicongyang.domain.TEtfHigherDTO;
import com.caicongyang.domain.TTransactionEtf;

import com.caicongyang.domain.TTransactionEtfDTO;
import java.text.ParseException;
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

    void calculateHigherStock(String tradingDay) throws ParseException;

    List<TEtfHigherDTO> getHigherEtf(String currentDate) throws ParseException;
}
