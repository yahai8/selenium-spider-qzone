package com.cn.selenium.spider.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author MuYaHai
 * @since 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("qq_friends")
public class QqFriends implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * 好友姓名
     */
    @TableField("friend_name")
    private String friendName;

    /**
     * 好友qq
     */
    @TableField("friend_qq")
    private String friendQq;

    /**
     * 添加时间
     */
    @TableField("create_time")
    private Date createTime;


}
