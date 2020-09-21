package com.cn.selenium.spider.entity.reponse;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 11:53
 */
@Data
@Accessors(chain = true)
public class Result<T> {
	private int code;
	private String message;
	private T data;


	public static <T> Result<T> SUCCESS(T data) {
		return new Result().setMessage("操作成功！").setData(data).setCode(200);
	}

	public static <T> Result<T> SUCCESS(T data, String message) {
		return new Result()
				.setCode(200)
				.setMessage(message)
				.setData(data);
	}

	public static Result FAIL(String message) {
		return new Result()
				.setCode(400)
				.setMessage(message);
	}


	public static Result FAIL(String message, int code) {
		return new Result()
				.setMessage(message)
				.setCode(code);
	}
}
