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
 * 用户表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_sys_user")
@ApiModel(value="SysUser对象", description="用户表")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "登录账号")
    private String code;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "1 有效；2 冻结")
    private Integer state;

    @ApiModelProperty(value = "用户角色：1 管理员；2 普通用户 ;3 商家")
    private Integer type;

    @ApiModelProperty(value = "性别 ：1 男 2 女")
    private String sex;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "qq号码")
    private String qqNumber;

    @ApiModelProperty(value = "微信号码")
    private String wxNumber;

    @ApiModelProperty(value = "总积分")
    private Float score;

    @ApiModelProperty(value = "存储商家个人简介")
    private String field0;

    @ApiModelProperty(value = "保存用户头像-saveName")
    private String field1;

    @ApiModelProperty(value = "冗余字段")
    private String field2;

    @ApiModelProperty(value = "冗余字段")
    private String field3;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
