package com.cn.selenium.spider.service;

import com.cn.selenium.spider.mq.param.MqParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 9:16
 */
@Service
public interface SpiderService {
	void loginQqZone(String qq, String pwd, String driverPath, List<String> typeList);

	void get_photo(int gtk, Map friendMap, String qq, Map cookie);

	int getFriendInfo(MqParam mqParam);

	void getMsg(MqParam mqParam);
}
