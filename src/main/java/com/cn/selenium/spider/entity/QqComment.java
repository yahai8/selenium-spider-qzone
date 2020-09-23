package com.cn.selenium.spider.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

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
 * @since 2020-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("qq_comment")
public class QqComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    /**
     * 评论人姓名
     */
    private String name;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 含中文时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 全数字
     */
    @TableField("create_time2")
    private String createTime2;

    /**
     * 时间戳
     */
    @TableField("create_time3")
    private String createTime3;

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 评论人qq号
     */
    @TableField("qq_num")
    private String qqNum;


}
