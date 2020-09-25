package com.cn.selenium.spider.mq;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import com.cn.selenium.spider.entity.reponse.Result;
import com.cn.selenium.spider.entity.request.RequestParams;
import com.cn.selenium.spider.mq.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: MuYaHai
 * Date: 2020/9/10, Time: 16:11
 */
@Component
public class RabbitMqSender {

	@Resource
	private AmqpTemplate rabbitTemplate;

	public Result send(String requestParams) {
		this.rabbitTemplate.convertAndSend(RabbitConfig.LOGIN_QqZONE_QUEUE, requestParams);
		return Result.SUCCESS("操作成功即将跳转到日志界面");
	}

	public Result sendToPhoto(String mqParam) {
		this.rabbitTemplate.convertAndSend(RabbitConfig.GET_FRIEND_PHOTO_ALBUM_QUEUE, mqParam);
		return Result.SUCCESS("操作成功即将跳转到日志界面");
	}

	public Result sendToGetInfo(String mqParam) {
		this.rabbitTemplate.convertAndSend(RabbitConfig.GET_FRIEND_INFO_QUEUE, mqParam);
		return Result.SUCCESS("操作成功！");
	}

	public Result sendGetMsg(String mqParam) {
		this.rabbitTemplate.convertAndSend(RabbitConfig.GET_FRIEND_MSG_QUEUE, mqParam);
		return Result.SUCCESS("操作成功！");
	}
}
