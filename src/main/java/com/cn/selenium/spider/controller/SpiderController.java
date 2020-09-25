package com.cn.selenium.spider.controller;

import com.alibaba.fastjson.JSON;
import com.cn.selenium.spider.entity.reponse.Result;
import com.cn.selenium.spider.entity.request.RequestParams;
import com.cn.selenium.spider.service.SpiderService;
import com.cn.selenium.spider.mq.RabbitMqSender;
import com.cn.selenium.spider.socket.WebSocket;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 9:15
 */
@RestController
@RequestMapping("/spider")
public class SpiderController {

	@Resource
	SpiderService spiderService;
	@Resource
	WebSocket webSocket;
	@Resource
	RabbitMqSender rabbitMqSender;

	@RequestMapping("/run")
	public Result spider(@RequestBody RequestParams requestParams) {
		try {
			return rabbitMqSender.send(JSON.toJSONString(requestParams));
		} catch (Exception e) {
			e.printStackTrace();
			return Result.FAIL("操作失败！");
		}
	}


	@GetMapping("/socket/{userName}")
	public Result socket(@PathVariable(value = "userName") String userName) {
		try {
			webSocket.sendAllMessage(userName);
			return Result.SUCCESS(null, "操作成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.FAIL("操作失败！");
		}
	}
}