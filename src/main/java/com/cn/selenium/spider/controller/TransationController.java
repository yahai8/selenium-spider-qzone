package com.cn.selenium.spider.controller;

import com.cn.selenium.spider.entity.reponse.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author: MuYaHai
 * Date: 2020/9/25, Time: 15:02
 */
@Slf4j
@RestController
@RequestMapping("/vue-element-admin/transaction")
public class TransationController {

	@GetMapping("/list")
	public Result list() {
		return Result.SUCCESS(new ArrayList<>());
	}
}
