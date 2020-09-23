package com.cn.selenium.spider.entity;

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
@TableName("qq_article")
public class QqArticle implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId
    private Long id;
    /**
     * 名字
     */
    private String name;

    /**
     * qq号
     */
    @TableField("qq_num")
    private String qqNum;

    /**
     * 说说内容
     */
    private String content;

    /**
     * 发布时间
     */
    @TableField("create_time")
    private String createTime;

    @TableField("friend_qq")
    private String friendQq;
}
