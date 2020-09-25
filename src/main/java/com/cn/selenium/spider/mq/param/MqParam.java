package com.cn.selenium.spider.mq.param;

import lombok.Data;

import java.util.Map;

/**
 * @author: MuYaHai
 * Date: 2020/9/24, Time: 13:58
 */
@Data
public class MqParam {

	private int gtk;
	private String token;
	private Map cookieMap;
	private Map friendMap;
	private String qq;
	private int count;
}
