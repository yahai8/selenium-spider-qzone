package com.cn.selenium.spider.entity.request;

import lombok.Data;

import java.util.List;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 11:52
 */
@Data
public class RequestParams {
	private String username;
	private String pass;
	private String driverPath;
	private List<String> typeList;
}
