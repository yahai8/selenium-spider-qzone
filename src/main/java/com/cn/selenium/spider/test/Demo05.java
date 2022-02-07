package com.cn.selenium.spider.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author: MuYaHai
 * Date: 2021/3/24, Time: 15:59
 */
public class Demo05 {
	public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InstantiationException {
		String str = "亚卓学院/ding7f34f9a908f7d0a324f2f5cc6abecb85/444934564227172522/learn_platform/documents/劳动创造历史.ppt";
		String[] split = str.split("source/", str.length());
		System.out.println(Arrays.toString(split));

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
		String format = simpleDateFormat.format(new Date());
		System.out.println(format);
	}
}
