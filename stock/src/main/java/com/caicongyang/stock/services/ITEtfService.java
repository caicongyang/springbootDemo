package com.caicongyang.stock.services;

import com.caicongyang.stock.domain.TEtf;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caicongyang.stock.domain.TEtfHigherDTO;
import com.caicongyang.stock.domain.TTransactionEtf;

import com.caicongyang.stock.domain.TTransactionEtfDTO;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author caicongyang
 * @since 2020-07-08
 */
public interface ITEtfService extends IService<TEtf> {

    List<TTransactionEtfDTO> querySortEtfStockData(String currentDate) throws IOException;

    List<TTransactionEtf> catchTransactionStockData(String currentDate);

    List<TTransactionEtfDTO> getTransactionEtfData(String currentDate) throws IOException;

    void calculateHigherStock(String tradingDay) throws ParseException;

    List<TEtfHigherDTO> getHigherEtf(String currentDate) throws ParseException, IOException;
}
