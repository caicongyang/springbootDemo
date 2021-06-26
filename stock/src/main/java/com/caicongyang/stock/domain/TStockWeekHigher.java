package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.io.Serializable;
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
 * @since 2021-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_Stock_Week_Higher")
@ApiModel(value="TStockWeekHigher对象", description="")
public class TStockWeekHigher implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "多少天内创新高")
    private Integer intervalDays;

    @ApiModelProperty(value = "交易日期")
    private LocalDate tradingDay;

    @ApiModelProperty(value = "前期高点日期")
    private LocalDate previousHighsDate;


}
