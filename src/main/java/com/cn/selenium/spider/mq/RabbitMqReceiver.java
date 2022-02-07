package com.cn.selenium.spider.mq;

import com.alibaba.fastjson.JSON;
import com.cn.selenium.spider.entity.request.RequestParams;
import com.cn.selenium.spider.mq.config.RabbitConfig;
import com.cn.selenium.spider.mq.param.MqParam;
import com.cn.selenium.spider.service.SpiderService;
import com.rabbitmq.client.impl.AMQImpl;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: MuYaHai
 * Date: 2020/9/10, Time: 16:16
 */
@Component

public class RabbitMqReceiver {
	@Resource
	SpiderService spiderService;

	@RabbitHandler
	@RabbitListener(queues = {RabbitConfig.LOGIN_QqZONE_QUEUE})
	public void loginQqZone(String params) {
		RequestParams requestParams = JSON.toJavaObject(JSON.parseObject(params), RequestParams.class);
		spiderService.loginQqZone(requestParams.getUsername(), requestParams.getPass(), requestParams.getDriverPath(),requestParams.getTypeList());
	}

	@RabbitHandler
	@RabbitListener(queues = {RabbitConfig.GET_FRIEND_INFO_QUEUE})
	public void getFriendInfo(String params) {
		MqParam mqParam = JSON.toJavaObject(JSON.parseObject(params), MqParam.class);
		spiderService.getFriendInfo(mqParam);
	}


	@RabbitHandler
	@RabbitListener(queues = {RabbitConfig.GET_FRIEND_PHOTO_ALBUM_QUEUE})
	public void getFriendPhotoAlbum(String params) {
		MqParam mqParam = JSON.toJavaObject(JSON.parseObject(params), MqParam.class);
		spiderService.get_photo(mqParam.getGtk(), mqParam.getFriendMap(), mqParam.getQq(), mqParam.getCookieMap());
	}

	@RabbitHandler
	@RabbitListener(queues = {RabbitConfig.GET_FRIEND_MSG_QUEUE})
	public void getMsg(String params) {
		MqParam mqParam = JSON.toJavaObject(JSON.parseObject(params), MqParam.class);
		spiderService.getMsg(mqParam);
	}


	@RabbitHandler
	@RabbitListener(queues = {RabbitConfig.TEST_QUEUE})
	public void testMq(Map<String,Object> params) {
		System.out.println(params);
	}
}
