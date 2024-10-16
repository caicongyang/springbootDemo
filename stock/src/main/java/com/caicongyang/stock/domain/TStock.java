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
 * @since 2020-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_stock")
@ApiModel(value="TStock对象", description="")
public class TStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "交易日期")
    private Date tradeDate;

    @ApiModelProperty(value = "开盘价")
    private Double open;

    @ApiModelProperty(value = "收盘价")
    private Double close;

    @ApiModelProperty(value = "最高价")
    private Double high;

    @ApiModelProperty(value = "最低价")
    private Double low;

    @ApiModelProperty(value = "成交量")
    private Long volume;

    @ApiModelProperty(value = "成交金额")
    private Double amount;

    @ApiModelProperty(value = "昨日收盘点")
    private Double preClose;

    @ApiModelProperty(value = "涨跌点")
    private Double change;

    @ApiModelProperty(value = "涨跌幅（%）")
    private Double pctChg;


}
