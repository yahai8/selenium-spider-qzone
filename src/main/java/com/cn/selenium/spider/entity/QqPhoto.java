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
@TableName("qq_photo")
public class QqPhoto implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * 真实地址
     */
    private String url;

    /**
     * 本地地址
     */
    @TableField("local_url")
    private String localUrl;

    /**
     * 名字腾讯官方保存的是时间
     */
    private String name;

    /**
     * 图片描述
     */
    private String desc;

    /**
     * 好友上传时间
     */
    @TableField("upload_time")
    private String uploadTime;

    /**
     * 相册名
     */
    @TableField("photo_album")
    private String photoAlbum;

    /**
     * 相册id
     */
    @TableField("photo_album_id")
    private Long photoAlbumId;

    /**
     * 入库时间
     */
    @TableField("create_time")
    private Date createTime;


}
