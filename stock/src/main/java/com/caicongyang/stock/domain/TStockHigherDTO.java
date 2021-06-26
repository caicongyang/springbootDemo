package com.caicongyang.stock.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
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
@ApiModel(value = "TStockHigher对象", description = "")
public class TStockHigherDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "名称")
    private String stockName;

    @ApiModelProperty(value = "多少天内创新高")
    private Integer intervalDays;

    @ApiModelProperty(value = "交易日期")
    private LocalDate tradingDay;

    @ApiModelProperty(value = "前期高点日期")
    private LocalDate previousHighsDate;

    @ApiModelProperty(value = "申万行业")
    private String swL3;

    @ApiModelProperty(value = "聚宽行业")
    private String jqL2;

    private String zjw;


}
