package com.cn.selenium.spider.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.ConstructorArgs;

/**
 * <p>
 * 
 * </p>
 *
 * @author MuYaHai
 * @since 2020-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("qq_log")
@AllArgsConstructor
@NoArgsConstructor
public class QqLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private long id;

    /**
     * 登录的qq号
     */
    private String qq;

    /**
     * 爬取人qq号
     */
    @TableField("spider_qq")
    private String spiderQq;

    /**
     * 0 成功，1 失败
     */
    @TableField("is_success")
    private Integer isSuccess;

    /**
     * 失败日志
     */
    @TableField("exception_content")
    private String exceptionContent;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    private String message;

    public static QqLog SUCCESS(String spiderQq, String message) {
        QqLog qqLog = new QqLog();
        qqLog.setCreateTime(new Date());
        qqLog.setIsSuccess(0);
        qqLog.setSpiderQq(spiderQq);
        qqLog.setMessage(message);
        return qqLog;
    }

    public static QqLog FAIL(String spiderQq,String exceptionContent,String message) {
        QqLog qqLog = new QqLog();
        qqLog.setCreateTime(new Date());
        qqLog.setIsSuccess(1);
        qqLog.setSpiderQq(spiderQq);
        qqLog.setMessage(message);
        qqLog.setExceptionContent(exceptionContent);
        return qqLog;
    }
}
