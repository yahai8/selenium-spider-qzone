package com.cn.selenium.spider.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
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
@ToString
public class QqLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

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

    private Integer year;

    private Integer month;

    private Integer day;

    @TableField("detail_time")
    private String detailTime;

    public static QqLog SUCCESS(String spiderQq, String message) {
        QqLog qqLog = new QqLog();
//        qqLog.setCreateTime(new Date());
        qqLog.setIsSuccess(0);
        qqLog.setSpiderQq(spiderQq);
        qqLog.setMessage(message);
        return qqLog;
    }

    public static QqLog FAIL(String spiderQq,String exceptionContent,String message) {
        QqLog qqLog = new QqLog();
//        qqLog.setCreateTime(new Date());
        qqLog.setIsSuccess(1);
        qqLog.setSpiderQq(spiderQq);
        qqLog.setMessage(message);
        qqLog.setExceptionContent(exceptionContent);
        return qqLog;
    }

    @Override
    public String toString() {
        return "QqLog{" +
                "id=" + id +
                ", qq='" + qq + '\'' +
                ", spiderQq='" + spiderQq + '\'' +
                ", isSuccess=" + isSuccess +
                ", exceptionContent='" + exceptionContent + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", message='" + message + '\'' +
                '}';
    }
}
