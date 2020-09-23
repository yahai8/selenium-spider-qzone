package com.cn.selenium.spider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import com.cn.selenium.spider.entity.reponse.Result;
import com.cn.selenium.spider.entity.request.RequestParams;
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
		this.rabbitTemplate.convertAndSend("hello", requestParams);
		return Result.SUCCESS("操作成功即将跳转到日志界面");
	}
}
