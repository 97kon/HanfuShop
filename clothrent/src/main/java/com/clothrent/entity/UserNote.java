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
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 帖子表
 * </p>
 *
 * @author liuqiming
 * @since 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_note")
@ApiModel(value="UserNote对象", description="帖子表")
public class UserNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;


    @ApiModelProperty(value = "回复根Id")
    private Long replyId;

    @ApiModelProperty(value = "回复人Id")
    private Long replyUserId;

    @ApiModelProperty(value = "回复人名称")
    private String replyUserName;


    @ApiModelProperty(value = "帖子ID")
    private Long noteId;

    @ApiModelProperty(value = "帖子标题")
    private String noteTitle;

    @ApiModelProperty(value = "动作类型：1 点赞；2 收藏；3 评论")
    private Integer actionType;

    @ApiModelProperty(value = "如果是评论则存储评论内容")
    private String comment;

    @ApiModelProperty(value = "用户头像")
    private String userImg;

    @ApiModelProperty(value = "帖子封面")
    private String noteImg;

    private String field0;

    @ApiModelProperty(value = "冗余字段1")
    private String field1;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<UserNote> childList;


}
