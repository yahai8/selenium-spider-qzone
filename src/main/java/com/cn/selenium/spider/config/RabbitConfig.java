package com.cn.selenium.spider.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author: MuYaHai
 * Date: 2020/9/10, Time: 16:19
 */
@Configuration
public class RabbitConfig {

	@Bean
	public Queue hellQueue() {
		return new Queue("hello");
	}
}
