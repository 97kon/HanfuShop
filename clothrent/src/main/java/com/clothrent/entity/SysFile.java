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
 * 附件表，存放文件
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_sys_file")
@ApiModel(value="SysFile对象", description="附件表，存放文件")
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "原始文件名")
    private String fileName;

    @ApiModelProperty(value = "所属对象ID")
    private Long objectId;

    @ApiModelProperty(value = "文件分类编码：0000-用户头像；0001--首页轮播图")
    private String categoryCode;

    @ApiModelProperty(value = "附件类型")
    private String fileType;

    @ApiModelProperty(value = "文件保存路径")
    private String fileUrl;

    @ApiModelProperty(value = "文件大小单位KB")
    private String fileSize;

    @ApiModelProperty(value = "实际保存的文件名字")
    private String saveName;

    private String field0;

    @ApiModelProperty(value = "冗余字段1")
    private String field1;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
