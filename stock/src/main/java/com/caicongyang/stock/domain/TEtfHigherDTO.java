package com.caicongyang.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2020-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TEtfHigher对象", description="")
public class TEtfHigherDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "多少天内创新高")
    private Integer intervalDays;

    @ApiModelProperty(value = "交易日期")
    private LocalDate tradingDay;

    @ApiModelProperty(value = "前期高点日期")
    private LocalDate previousHighsDate;

    private String stockName;


}
