package com.cn.selenium.spider.controller;

import com.cn.selenium.spider.entity.reponse.Result;
import com.cn.selenium.spider.entity.request.RequestParams;
import com.cn.selenium.spider.service.SpiderService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@RequestMapping("/run")
	public Result spider(@RequestBody RequestParams requestParams) {
		try {
			spiderService.loginQqZone(requestParams.getQq(), requestParams.getPassword(), requestParams.getDriverPath());
		} catch (Exception e) {
			e.printStackTrace();
			return Result.FAIL("操作失败！");
		}
		return Result.SUCCESS(null);
	}
}
