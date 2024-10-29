package com.clothrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 订单表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_sys_order")
@ApiModel(value="SysOrder对象", description="订单表")
public class SysOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @TableId(value = "商家ID")
    private Long salerId;

    @ApiModelProperty(value = "商家名称")
    private String salerName;

    @ApiModelProperty(value = "订单号")
    private String code;

    @ApiModelProperty(value = "状态：0 待支付；1 已支付；2 已确认；3 已发货 ；4 已收货；5 申请归还；6 已归还；7 归还驳回；")
    private Integer state;

    @ApiModelProperty(value = "总价格")
    private BigDecimal price;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "收货地址ID")
    private Long userAddressId;

    @ApiModelProperty(value = "收货地址")
    private String address;

    @ApiModelProperty(value = "物流单号")
    private String expressNum;

    @ApiModelProperty(value = "驳回缘由")
    private String refuseDesc;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    private String field0;

    @ApiModelProperty(value = "冗余字段1")
    private String field1;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SysOrderItem> childList;


}
