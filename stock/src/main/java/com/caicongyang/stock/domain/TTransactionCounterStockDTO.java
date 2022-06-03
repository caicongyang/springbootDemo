package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
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
@ApiModel(value = "TTransactionStock对象", description = "")
public class TTransactionCounterStockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stockCode;

    private String stockName;


    private String tradingDay;

    @ApiModelProperty(value = "申万行业")
    private String swL3;

    @ApiModelProperty(value = "聚宽行业")
    private String jqL2;

    @ApiModelProperty(value = "证监会行业")
    private String zjw;

    @ApiModelProperty(value = "出现次数")
    private Long counter;

    @ApiModelProperty(value = "证监会行业")
    private Double gain;


}
