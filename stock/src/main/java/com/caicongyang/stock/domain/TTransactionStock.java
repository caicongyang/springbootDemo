package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@TableName("T_transaction_stock")
@ApiModel(value = "TTransactionStock对象", description = "")
public class TTransactionStock implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stockCode;

    private Double lastDayCompare;

    private Double meanRatio;

    private String conceptStr;

    private String tradingDay;

    @ApiModelProperty(value = "申万三级行业")
    private String swL3;

    @ApiModelProperty(value = "聚宽二级行业")
    private String jqL2;

    @ApiModelProperty(value = "证监会行业")
    private String zjw;

    @ApiModelProperty(value = "当日涨幅")
    private Double gain;


}
