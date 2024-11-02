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
 * 商品评价表
 * </p>
 *
 * @author liuqiming
 * @since 2023-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_goods_appraise")
@ApiModel(value="GoodsAppraise对象", description="商品评价表")
public class GoodsAppraise implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户图像")
    private String userImg;

    @ApiModelProperty(value = "商品主图")
    private String goodsImg;

    @ApiModelProperty(value = "评分")
    private Integer score;

    @ApiModelProperty(value = "评价内容")
    private String content;

    @ApiModelProperty(value = "关联的订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单明细ID")
    private Long orderItemId;

    @ApiModelProperty(value = "评价图片")
    private String image;

    @ApiModelProperty(value = "备用字段：好评解释")
    private String replyInfo;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
