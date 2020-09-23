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
@TableName("qq_source")
public class QqSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    /**
     * 照片/视频空间地址
     */
    private String url;

    /**
     * 照片/视频本地地址
     */
    @TableField("url_local")
    private String urlLocal;

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;


}
