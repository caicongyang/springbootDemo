package com.caicongyang.stock.domain;

import io.swagger.annotations.ApiModel;
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
 * @since 2022-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TStockLimit DTO", description = "")
public class TStockLimitDTO extends TStockLimit implements Serializable {

    private String stockName;

    private String swL3;

    private String jqL2;

    private String zjw;

    private Integer counter;

}
