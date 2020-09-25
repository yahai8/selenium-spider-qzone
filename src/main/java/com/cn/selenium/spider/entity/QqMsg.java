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
@TableName("qq_msg")
public class QqMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * 留言时间
     */
    @TableField("pub_time")
    private String pubTime;

    /**
     * 名称
     */
    private String nickname;

    /**
     * 留言内容
     */
    @TableField("ubb_content")
    private String ubbContent;

    /**
     * 好友名称
     */
    private String name;

    /**
     * 好友qq
     */
    private String qq;

    /**
     * 入库时间
     */
    @TableField("create_time")
    private Date createTime;


}
