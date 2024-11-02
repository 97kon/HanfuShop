package com.clothrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author liuqiming
 * @since 2024-02-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_sys_order_item")
@ApiModel(value="SysOrderItem对象", description="订单明细表")
public class SysOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    private String code;
    private String clothSize;

    @ApiModelProperty(value = "用品id")
    private Long goodsId;

    @ApiModelProperty(value = "用品名称")
    private String goodsName;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "商品购买数量")
    private Integer number;

    @ApiModelProperty(value = "保留字段，适用于拆单场景。状态：0 待支付；1 已支付；2 已确认；3 已发货 ；4 已收货；5 申请归还；6 已归还；7 归还驳回")
    private Integer state;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    @ApiModelProperty(value = "保存购买时物品快照，适用于快照场景，保留字段")
    private String field0;

    @ApiModelProperty(value = "商品主图")
    private String field1;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
