package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author caicongyang
 * @since 2022-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_Stock_Limit")
@ApiModel(value="TStockLimit对象", description="")
public class TStockLimit implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stockCode;

    private LocalDate tradingDay;

    @ApiModelProperty(value = "涨幅")
    private Double gain;


}
