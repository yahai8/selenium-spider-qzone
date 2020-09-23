package com.cn.selenium.spider.service.impl;

import com.alibaba.fastjson.JSON;
import com.cn.selenium.spider.entity.request.RequestParams;
import com.cn.selenium.spider.service.SpiderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: MuYaHai
 * Date: 2020/9/10, Time: 16:16
 */
@Component
@RabbitListener(queues = "hello")
public class RabbitMqReceiver {
	@Resource
	SpiderService spiderService;

	@RabbitHandler
	public void process(String params) {
		RequestParams requestParams = JSON.toJavaObject(JSON.parseObject(params), RequestParams.class);
		spiderService.loginQqZone(requestParams.getUsername(),requestParams.getPass(),requestParams.getDriverPath());
	}
}
