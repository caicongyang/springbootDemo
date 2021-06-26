package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_transaction_etf")
@ApiModel(value="TTransactionEtf对象", description="")
public class TTransactionEtf implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stockCode;

    private Double lastDayCompare;

    private Double meanRatio;

    private String tradingDay;

    @ApiModelProperty(value = "申万行业")
    private String swL3;

    @ApiModelProperty(value = "聚宽行业")
    private String jqL2;

    private String zjw;


}
