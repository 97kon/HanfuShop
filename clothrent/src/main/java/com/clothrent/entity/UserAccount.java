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
 * 用户账户表
 * </p>
 *
 * @author liuqiming
 * @since 2024-03-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_account")
@ApiModel(value="UserAccount对象", description="用户账户表")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "当前账户余额")
    private BigDecimal amount;

    @ApiModelProperty(value = "变动前余额")
    private BigDecimal beforeNum;

    @ApiModelProperty(value = "变动余额")
    private BigDecimal changeNum;

    @ApiModelProperty(value = "关联订单流水号")
    private String orderNum;

    @ApiModelProperty(value = "关联订单ID")
    private Long orderId;

    @ApiModelProperty(value = "变动说明")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
