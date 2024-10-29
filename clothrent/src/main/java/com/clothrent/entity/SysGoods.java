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
 * 商品表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_sys_goods")
@ApiModel(value="SysGoods对象", description="商品表")
public class SysGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableId(value = "商家ID")
    private Long userId;
    @ApiModelProperty(value = "商家名称")
    private String userName;

    @ApiModelProperty(value = "商品编码")
    private String code;

    private String clothSize;



    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    private String catgegoryCode;

    @ApiModelProperty(value = "二级分类ID")
    private Long secondCId;

    @ApiModelProperty(value = "二级分类名称")
    private String secondCName;

    @ApiModelProperty(value = "二级分类编码")
    private String secondCCode;

    @ApiModelProperty(value = "标签")
    private String tags;

    @ApiModelProperty(value = "状态：1 上架； 2下架")
    private Integer state;

    @ApiModelProperty(value = "当前库存")
    private Long stock;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;
    private BigDecimal feedbackRate;

    @ApiModelProperty(value = "折扣后价格")
    private BigDecimal discount;

    @ApiModelProperty(value = "折扣率，比如100%")
    private String discountRadio;

    @ApiModelProperty(value = "商品详情")
    private String content;

    @ApiModelProperty(value = "商品图片1")
    private String goodsPic1;

    @ApiModelProperty(value = "商品图片2")
    private String goodsPic2;

    @ApiModelProperty(value = "商品图片3")
    private String goodsPic3;

    @ApiModelProperty(value = "商品图片4")
    private String goodsPic4;

    @ApiModelProperty(value = "商品图片5")
    private String goodsPic5;


    @ApiModelProperty(value = "该商品历史总销量")
    private Integer saleNumber;
    private Integer commentNum;

    private String field0;

    @ApiModelProperty(value = "冗余字段1")
    private String field1;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
