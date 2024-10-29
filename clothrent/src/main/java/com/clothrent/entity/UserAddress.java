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
import java.time.LocalDateTime;

/**
 * <p>
 * 用户收货地址表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_address")
@ApiModel(value="UserAddress对象", description="用户收货地址表")
public class UserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属用户名称")
    private String userName;

    @ApiModelProperty(value = "所属用户ID")
    private Long userId;

    @ApiModelProperty(value = "收货人手机号")
    private String mobile;

    @ApiModelProperty(value = "收货人名称")
    private String receiverName;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "完整详细地址")
    private String address;

    @ApiModelProperty(value = "是否默认：0 否；1 是")
    private Integer isDefault;

    @ApiModelProperty(value = "备注")
    private String remark;

    private String field0;

    @ApiModelProperty(value = "冗余字段1")
    private String field1;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
