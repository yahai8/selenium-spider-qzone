package com.cn.selenium.spider.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

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
@TableName("qq_photo_album")
public class QqPhotoAlbum implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * 相册名
     */
    @TableField("album_name")
    private String albumName;

    /**
     * 创建相册时间
     */
    @TableField("upload_time")
    private Date uploadTime;

    /**
     * 相册描述
     */
    @TableField("album_desc")
    private String albumDesc;

    /**
     * 封面图
     */
    @TableField("pre_url")
    private String preUrl;

    /**
     * 入库时间
     */
    @TableField("create_time")
    private Date createTime;

    private String total;


    @TableField("friend_qq")
    private String friendQq;

    @TableField("friend_name")
    private String friendName;


    @TableField("local_url")
    private String localUrl;

    private Integer year;

    private Integer month;

    private Integer day;

    @TableField("detail_time")
    private String detailTime;
}
