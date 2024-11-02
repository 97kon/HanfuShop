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
 * 帖子表
 * </p>
 *
 * @author liuqiming
 * @since 2023-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_sys_note")
@ApiModel(value="SysNote对象", description="帖子表")
public class SysNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发布人ID")
    private Long userId;

    @ApiModelProperty(value = "发布人名称")
    private String userName;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "1.笔记；2 穿搭")
    private String type;

    @ApiModelProperty(value = "关联商品id")
    private Long goodsId;

    @ApiModelProperty(value = "关联商品名称")
    private String goodsName;

    @ApiModelProperty(value = "管理员发布的穿搭类型")
    private String category;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "点赞数")
    private Integer favorNum;

    @ApiModelProperty(value = "点赞数+收藏数+评论数")
    private Integer totalNum;

    @ApiModelProperty(value = "收藏数")
    private Integer collectNum;

    @ApiModelProperty(value = "评论数")
    private Integer commentNum;

    @ApiModelProperty(value = "冗余字段")
    private String field0;

    @ApiModelProperty(value = "保存封面主图")
    private String field1;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
