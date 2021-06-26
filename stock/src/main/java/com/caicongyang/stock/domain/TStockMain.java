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
 * @since 2020-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_Stock_Main")
@ApiModel(value="TStockMain对象", description="")
public class TStockMain implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stockCode;

    private String stockName;

    private String swL3;

    private String jqL2;

    private String zjw;

    private String type;


}
