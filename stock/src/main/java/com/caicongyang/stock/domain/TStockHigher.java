package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author caicongyang
 * @since 2020-10-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_Stock_Higher")
@ApiModel(value="TStockHigher对象", description="")
public class TStockHigher implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "多少天内创新高")
    private Integer intervalDays;

    @ApiModelProperty(value = "交易日期")
    private Date tradingDay;

    @ApiModelProperty(value = "前期高点日期")
    private Date previousHighsDate;

    @ApiModelProperty(value = "当日涨幅")
    private Double gain;


}
