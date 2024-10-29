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
 * 库存变动记录表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_inventory")
@ApiModel(value="UserInventory对象", description="库存变动记录表")
public class UserInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作员ID：入库的时候是管理员ID，出库的时候是用户ID")
    private Long userId;

    @ApiModelProperty(value = "操作员名称")
    private String userName;

    @ApiModelProperty(value = "当前库存")
    private Integer currentStock;

    @ApiModelProperty(value = "改变前的库存")
    private Integer beforeStock;

    @ApiModelProperty(value = "变动库存")
    private Integer changeStock;

    @ApiModelProperty(value = "类型：1 入库；2 出库")
    private Integer type;

    @ApiModelProperty(value = "用品ID")
    private Long goodsId;

    @ApiModelProperty(value = "用品名称")
    private String goodsName;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "本次采购/售出总费用")
    private BigDecimal price;

    @ApiModelProperty(value = "库存变动说明-系统自动生成")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "冗余字段")
    private String field0;

    @ApiModelProperty(value = "冗余字段1")
    private String field1;

    @ApiModelProperty(value = "冗余字段2")
    private String field2;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
