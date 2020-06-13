package com.caicongyang.domain;

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
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_transaction_tock")
@ApiModel(value="TTransactionTock对象", description="")
public class TTransactionTock implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stockCode;

    private Double lastDayCompare;

    private Double meanRatio;

    private String conceptStr;

    private String conceptList;

    private LocalDate tradingDay;


}
