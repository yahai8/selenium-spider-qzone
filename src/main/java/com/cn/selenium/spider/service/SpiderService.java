package com.cn.selenium.spider.service;

import org.springframework.stereotype.Service;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 9:16
 */
@Service
public interface SpiderService {
	public void loginQqZone(String qq,String pwd,String driverPath);
}
