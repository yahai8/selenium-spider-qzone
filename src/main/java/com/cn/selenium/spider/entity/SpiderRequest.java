package com.cn.selenium.spider.entity;

import lombok.Data;

/**
 * @author: MuYaHai
 * Date: 2020/9/18, Time: 17:25
 */
@Data
public class SpiderRequest {

	private String qqNum;
	private String password;
	private String filePath;
	private String driverPath;

}
