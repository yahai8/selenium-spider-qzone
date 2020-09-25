package com.cn.selenium.spider.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author: MuYaHai
 * Date: 2020/9/10, Time: 16:19
 */
@Configuration
public class RabbitConfig {
	public static final String LOGIN_QqZONE_QUEUE = "loginQqZone";
	public static final String GET_FRIEND_MSG_QUEUE = "getMsg";
	public static final String GET_FRIEND_INFO_QUEUE = "getFriendInfo";
	public static final String GET_FRIEND_PHOTO_ALBUM_QUEUE = "getFriendPhotoAlbum";

	@Bean
	public Queue loginQqZone() {
		return new Queue(LOGIN_QqZONE_QUEUE);
	}

	@Bean
	public Queue getMsg() {
		return new Queue(GET_FRIEND_MSG_QUEUE);
	}

	@Bean
	public Queue getFriendInfoQueue() {
		return new Queue(GET_FRIEND_INFO_QUEUE);
	}

	@Bean
	public Queue getFriendPhotoAlbumQueue() {
		return new Queue(GET_FRIEND_PHOTO_ALBUM_QUEUE);
	}
}
