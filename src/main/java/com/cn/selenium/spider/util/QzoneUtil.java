package com.cn.selenium.spider.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 9:23
 */
@Slf4j
public class QzoneUtil {
	public static void main(String[] args) {
		download(null, null);
	}

	/**
	 * 封装部分请求头信息
	 * @return
	 */
	public static Map get_headerMap() {
		HashMap<String, String> headersMap = new HashMap<String, String>();
		headersMap.put("host", "h5.qzone.qq.com");
		headersMap.put("accept-encoding", "gzip, deflate, br");
		headersMap.put("accept-language", "zh-CN,zh;q=0.8");
		headersMap.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headersMap.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0");
		headersMap.put("connection", "keep-alive");
		return headersMap;
	}


	/**
	 * 图片视频下载
	 * @param url
	 * @param friendMap
	 * @return
	 */
	public static String download(String url,Map friendMap){
		URL url1 = null;
		String dir = System.getProperty("user.dir")+"/selenium/src/main/resources/static/";
		String localPath = null;
		try {
			url1 = new URL(url);
			DataInputStream dataInputStream = new DataInputStream(url1.openStream());
			String path = null;
			String s = IdUtil.randomUUID();
			String name = (String) friendMap.get("name");
			Integer uin = (Integer) friendMap.get("uin");
			if (url.contains("video")) {
				path = dir +name +uin + "/" + s + ".mp4";
				localPath = name + uin + "/" + s + ".mp4";
			} else {
				path=  dir+ name +uin  + "/" + s + ".png";
				localPath = name + uin + "/" + s + ".png";
			}
			boolean exist = FileUtil.exist(path);
			if (!exist) {
				File file = FileUtil.newFile(path);
				long copy = IoUtil.copy(dataInputStream, FileUtil.getOutputStream(file));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error("url读取错误："+e);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("io读写异常：" + e);
			return null;
		}
		return localPath;
	}


	/**
	 * 通过正则匹配，在登陆成功之后的页面获取token
	 * @param pageSource
	 * @return
	 */
	public static String get_g_qZoneToken(String pageSource) {
		String regex = "window.g_qzonetoken = \\(function\\(\\)\\{ try\\{return \".*?\";\\} catch\\(e\\)";
		boolean contains = ReUtil.contains(regex, pageSource);
		System.out.println(contains);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(pageSource);
		System.out.println("str:" + pageSource);
		System.out.println("regex:"+regex);
		if (!matcher.find()) {
			log.error("token获取失败，即将终止！");
			return null;
		}
		String group = matcher.group(0);
		String substring = group.substring(group.indexOf("\"")+1, group.lastIndexOf("\""));
		System.out.println("matcher: " + substring);
		return substring;
	}


	/**
	 * g_tk参数必带，js文件有相关算法
	 * @param cookieMap
	 * @return
	 */
	public static int get_g_tk(Map cookieMap) {
		Integer hashes = 5381;
		String p_skey = (String) cookieMap.get("p_skey");
		for (int i = 0; i < p_skey.length(); i++) {
			hashes += (hashes << 5) + p_skey.charAt(i);
		}

		return hashes & 0x7fffffff;
	}


	/**
	 * 获取cookie
	 * @param chromeDriver
	 * @return
	 */
	public static Map get_cookie(ChromeDriver chromeDriver) {
		Set<Cookie> cookies = chromeDriver.manage().getCookies();
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		for (Cookie cookie : cookies) {
			hashMap.put(cookie.getName(), cookie.getValue());
		}
		return hashMap;
	}



	/**
	 * 获得运行轨迹
	 * @param distance
	 * @return
	 */
	public static List get_track(int distance) {
		List track = new ArrayList();
		//当前加速度运行的总距离
		int current = 0;
		//加速度运动的距离，distance-mid就是加速度为负的减速度运动，同向
		int mid = distance * 3 / 5;
		//时间
		int t = 1;
		//某个时刻的速度
		int v = 0;
		//加速度
		int a = 0;
		//每秒移动距离
		int move = 0;
		//初速度，到后边也算是每个时刻的速度
		int vo = 0;
		while (current < distance) {
			if (current < mid) {
				a = 2;
			} else {
				a = -3;
			}
			vo = v;
			v = (int) (vo + a * t);
			move = (int) (vo * t + (0.5) * a * t * t);
			current += move;
			if (current > distance) {
				move = distance - (current - move);
			}
			track.add(Math.round(move));
		}
		return track;

	}

	/**
	 * 获取页面资源
	 * @param url
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
	public static String get_response(String url,Map cookie) throws IOException {
		Connection connect = Jsoup.connect(url);
//		connect.proxy("118.24.12.41", 8080);
		HashMap<String, String> headersMap = new HashMap<String, String>();
		headersMap.put("host", "h5.qzone.qq.com");
		headersMap.put("accept-encoding", "gzip, deflate, br");
		headersMap.put("accept-language", "zh-CN,zh;q=0.8");
		headersMap.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headersMap.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0");
		headersMap.put("connection", "keep-alive");
		Document document = connect.userAgent("Mozilla").cookies(cookie).headers(headersMap).get();
		return document.text();
	}


	/**
	 * 判断标签是否存在
	 * @param chromeDriver
	 * @param id
	 * @return
	 */
	public static WebElement isElementExist(ChromeDriver chromeDriver, String id) {
		try {
			return chromeDriver.findElementById(id);
		} catch (Exception e) {
			return null;
		}
	}
}
