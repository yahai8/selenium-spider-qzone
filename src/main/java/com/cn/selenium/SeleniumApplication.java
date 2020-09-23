package com.cn.selenium;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: MuYaHai
 * Date: 2020/9/18, Time: 16:16
 */
@SpringBootApplication
@ComponentScan("com.cn.selenium.spider")
@MapperScan("com.cn.selenium.spider.mapper")
public class SeleniumApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeleniumApplication.class);
	}
}
