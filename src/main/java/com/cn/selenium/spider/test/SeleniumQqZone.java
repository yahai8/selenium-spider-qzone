package com.cn.selenium.spider.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.interactions.Actions;

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
 * Date: 2020/9/16, Time: 16:49
 */
@Slf4j
public class SeleniumQqZone {
	public static void main(String[] args) throws IOException {
		loginQqZone();
	}

	private static void loginQqZone() {
		System.setProperty("webdriver.chrome.driver", "c:/driver/chromedriver.exe");
		ChromeDriver chromeDriver = new ChromeDriver();
		try {
			chromeDriver.get("https://i.qq.com/");
			String title = chromeDriver.getTitle();
			System.out.println(title + "...........");
			WebElement loginFrame = chromeDriver.findElementById("login_frame");
			chromeDriver.switchTo().frame(loginFrame);
			Thread.sleep(2000);
			chromeDriver.findElementById("switcher_plogin").click();
			WebElement username = chromeDriver.findElementById("u");
			System.out.println("输入用户名...........");
			Thread.sleep(2000);
			username.sendKeys("114161171");
			WebElement password = chromeDriver.findElementById("p");
			System.out.println("输入密码...........");
			Thread.sleep(2000);
			password.sendKeys("qwerty520.1314");
			WebElement login = chromeDriver.findElementById("login_button");
			System.out.println("点击登录...........");
			Thread.sleep(5000);
			login.click();
			Thread.sleep(2000);
			WebElement tcaptchaIframe = isElementExist(chromeDriver,"tcaptcha_iframe");
			if (tcaptchaIframe != null) {
				System.out.println("图形验证码...........");
				Thread.sleep(2000);
				chromeDriver.switchTo().frame(tcaptchaIframe);
				Thread.sleep(2000);
				WebElement slideBlock = chromeDriver.findElementById("slideBlock");
				Thread.sleep(2000);
				boolean flag = true;
				int distance = 192-22;
				int offset = 5;
				int times = 0;
				Actions actions = new Actions(chromeDriver);
				actions.clickAndHold(slideBlock).perform();
				while (flag) {
					List list = get_track(distance);
					for (Object i : list) {
						actions.moveByOffset((Integer) i, 0).perform();
						System.out.println("滑块的css:" + slideBlock.getCssValue("left"));
						WebElement guideText = chromeDriver.findElementById("guideText");
						System.out.println("操作消息通知：" + guideText.getText());
						if ("192px".equals(slideBlock.getCssValue("left"))) {
							System.out.println("验证成功");
							flag = false;
							break;
						}
						if ("拖动下方滑块完成拼图".equals(guideText.getText()) || "请控制拼图块对齐缺口".equals(guideText.getText()) || "这题有点难呢，已为您更换题目".equals(guideText.getText())) {
							System.out.println("重试图形拖动验证码");
							Thread.sleep(2000);
						} else {
							System.out.println("验证成功");
							flag = false;
							break;
						}
					}
				}
				actions.release(slideBlock).perform();
			}
			Thread.sleep(2000);
			Map cookie = get_cookie(chromeDriver);
			get_FriendInfo(get_g_tk(cookie),get_g_qZoneToken(chromeDriver.getPageSource()),cookie);
			String currentUrl = chromeDriver.getCurrentUrl();
			chromeDriver.getLocalStorage();
			chromeDriver.findElementById("aMyFriends").click();
			Thread.sleep(2000);
			List<WebElement> appCanvasFrame = chromeDriver.findElementsByClassName("app_canvas_frame");
			chromeDriver.switchTo().frame(appCanvasFrame.get(0));
			List<WebElement> elementsByClassName = chromeDriver.findElementsByClassName("textoverflow");
			for (WebElement webElement : elementsByClassName) {
				Thread.sleep(2000);
				webElement.click();
				Thread.sleep(2000);
				WebElement friendshipPromoteLayer = isElementExist(chromeDriver, "friendship_promote_layer");
				if (friendshipPromoteLayer != null) {
					WebElement btnFsSure = chromeDriver.findElementByClassName("btn-fs-sure");
					btnFsSure.click();
					Thread.sleep(2000);
				}
				LocalStorage localStorage = chromeDriver.getLocalStorage();
				get_cookie(chromeDriver);
				WebElement profileMoodA = chromeDriver.findElementById("QM_Profile_Mood_A");
				profileMoodA.click();
				WebElement qmFeedsContainer = chromeDriver.findElementById("QM_Feeds_Container");
				WebElement qm_feeds_iframe = qmFeedsContainer.findElement(By.id("QM_Feeds_Iframe"));
				WebElement qmFeedsIframe = chromeDriver.findElementById("QM_Feeds_Iframe");
				chromeDriver.switchTo().frame(qmFeedsIframe);
				List<WebElement> hostHomeFeeds = chromeDriver.findElementsById("host_home_feeds");
				for (WebElement hostHomeFeed : hostHomeFeeds) {
					WebElement fName = hostHomeFeed.findElement(By.className("f-name q_namecard "));
					System.out.println(fName.getText());
					WebElement timeState = hostHomeFeed.findElement(By.className(" ui-mr8 state"));
					System.out.println(timeState.getText());
					WebElement fInfo = hostHomeFeed.findElement(By.className("f-info"));
					System.out.println(fInfo.getText());
					WebElement readTimes = hostHomeFeed.findElement(By.className("state qz_feed_plugin"));
					System.out.println(readTimes.getText());
					List<WebElement> elementList = hostHomeFeed.findElements(By.className("item q_namecard"));
					StringBuffer buffer = new StringBuffer();
					for (WebElement element : elementList) {
						buffer.append("\t"+element.getText());
					}
					System.out.println(buffer.toString());
				}
				System.out.println(chromeDriver.getPageSource());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			chromeDriver.close();
		}
	}

	private static Map get_cookie(ChromeDriver chromeDriver) {
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

//	"http://photogz.photo.store.qq.com/psc?/V10SLzgg0EtXFL/ruAMsa53pVQWN7FLK88i5v2SOVwDKkhFgzI37r6f7HqVmEdD.DEc*DE*4SjJMX4kukxLzNqUstmURGicf*mDvJ4Ea6tqhfHUVIyw3*9Hob0!/b&amp;bo=QAa7BqsLkgwREOI!
//	"http://photogz.photo.store.qq.com/psc?/V10SLzgg0EtXFL/ruAMsa53pVQWN7FLK88i5v2SOVwDKkhFgzI37r6f7HqVmEdD.DEc*DE*4SjJMX4kukxLzNqUstmURGicf*mDvJ4Ea6tqhfHUVIyw3*9Hob0!/b&amp;bo=QAa7BqsLkgwREOI!"
//	"http://photogz.photo.store.qq.com/psc?/V10SLzgg0EtXFL/ruAMsa53pVQWN7FLK88i5v2SOVwDKkhFgzI37r6f7HpybUTxujinskYzrRfULsKn7OOnbqOluncUCfwVZyal2pifmwP008Rff8wiwtzF6Oo!/b&amp;bo=QAYcC3AIAA8REAE!&amp;rf=mood_app&amp;t=5"
//	"http://photogz.photo.store.qq.com/psc?/V10SLzgg0EtXFL/ruAMsa53pVQWN7FLK88i5v2SOVwDKkhFgzI37r6f7HpybUTxujinskYzrRfULsKn7OOnbqOluncUCfwVZyal2pifmwP008Rff8wiwtzF6Oo!/b&amp;bo=QAYcC3AIAA8REAE!"


	/**
	 * 判断标签是否存在
	 * @param chromeDriver
	 * @param id
	 * @return
	 */
	public static WebElement isElementExist(ChromeDriver chromeDriver,String id) {
		try {
			return chromeDriver.findElementById(id);
		} catch (Exception e) {
			return null;
		}
	}
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


	public static int get_g_tk(Map cookieMap) {
		Integer hashes = 5381;
		String p_skey = (String) cookieMap.get("p_skey");
		for (int i = 0; i < p_skey.length(); i++) {
			hashes += (hashes << 5) + p_skey.charAt(i);
		}

		return hashes & 0x7fffffff;
	}


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


	public static List<Map> get_AllFriends(int gtk, String token, Map cookie) {
		String url = "https://user.qzone.qq.com/proxy/domain/r.qzone.qq.com/cgi-bin/tfriend/friend_ship_manager.cgi?uin=114161171&do=1&fupdate=1&clean=1&g_tk=" + gtk + "&qzonetoken=" + token;
		try {
			String pageSource = get_response(url, cookie);
			String regx = "\"uin\":(.*?), \"name\":(.*?),";
			Pattern pattern = Pattern.compile(regx);
			Matcher matcher = pattern.matcher(pageSource);
			StringBuffer buffer = new StringBuffer();
			buffer.append("[");
			while (matcher.find()) {
				String removeSuffix = StrUtil.removeSuffix(matcher.group(0), ",");
				buffer.append("{");
				buffer.append(removeSuffix);
				buffer.append("},");
			}
			String suffix = StrUtil.removeSuffix(buffer.toString(), ",");
			JSONArray jsonArray = JSON.parseArray(suffix+"]");
			cn.hutool.json.JSONArray jsonArray1 = JSONUtil.parseArray(suffix + "]");
			List<Map> mapList = JSONUtil.toList(jsonArray1, Map.class);
			return mapList;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("获取好友列表失败！");
			return null;
		}
	}


	public static void get_FriendInfo(int gtk,String token,Map cookie) throws IOException {
		List<Map> allFriends = get_AllFriends(gtk, token,cookie);
		for (Map friend : allFriends) {
			Integer qq = (Integer) friend.get("uin");
			String url = "https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin="+qq+"&inCharset=utf-8&outCharset=utf-8&hostUin='+str(qq)+'&notice=0&sort=0&pos=0&num=20&cgi_host=http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6&code_version=1&format=jsonp&need_private_comment=1&g_tk="+gtk+"&qzonetoken="+token;
			String response = get_response(url, cookie);
			String sub = StrUtil.sub(response, response.indexOf("(")+1, response.lastIndexOf(")"));
			JSONObject jsonObject = JSONObject.parseObject(sub);
			//总说说内容
			JSONArray msglist = (JSONArray) jsonObject.get("msglist");
			if (msglist != null && msglist.size() > 0) {
				for (int i = 0; i < msglist.size(); i++) {
					JSONObject object = (JSONObject) msglist.get(i);
					Object name = object.get("name");
					log.info("发布人：" + name);
					//说说内容
					Object content = object.get("content");
					//发布说说的时间
					Object createTime = object.get("createTime");
					//时间戳
					Object createdTime = object.get("created_time");
					log.info("发布时间："+createTime);
					log.info("发布内容:"+content);
					//评论内容
					JSONArray commentlist = (JSONArray) object.get("commentlist");
					log.info("=======================评论内容开始======================");
					if (commentlist != null && commentlist.size() > 0) {
						for (int j = 0; j < commentlist.size(); j++) {
							log.info(".............................");
							JSONObject comment = (JSONObject) commentlist.get(i);
							//评论人姓名
							Object commentName = comment.get("name");
							log.info("评论人姓名："+commentName);
							//评论内容
							Object commentContent = comment.get("content");
							log.info("评论内容："+commentContent);
							//含中文时间
							Object commentTime = comment.get("createTime");
							//全数字
							Object commentTime2 = comment.get("createTime2");
							//时间戳
							Object commentTime3 = comment.get("create_time");
							log.info("评论时间："+commentTime3);
							//评论人qq
							Object commentQQ = comment.get("uin");
							log.info("评论人qq号码："+commentQQ);
						}
					}
					log.info("=======================评论内容结束======================");
					//照片列表
					JSONArray picList = (JSONArray) object.get("pic");
					if (picList != null && picList.size() > 0) {
						for (int j = 0; j < picList.size(); j++) {
							JSONObject picElement = (JSONObject) picList.get(j);
							//获取照片
							Object picId = picElement.get("pic_id");
							Object absolutePosition = picElement.get("absolute_position");
							log.info("计数："+absolutePosition);
							log.info("照片地址：" + picId);
							download((String) picId, friend);
						}
					}
					//视频
					JSONArray videoList = (JSONArray) object.get("video");
					if (videoList != null && videoList.size() > 0) {
						for (int j = 0; j < videoList.size(); j++) {
							JSONObject video = (JSONObject) videoList.get(j);
							Object url3 = video.get("url3");
							log.info("视频地址："+url3);
							download((String) url3, friend);
						}
					}
				}
			}

		}
	}


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


	public static boolean download(String url,Map friendMap){
		URL url1 = null;
		try {
			url1 = new URL(url);

		DataInputStream dataInputStream = new DataInputStream(url1.openStream());
		String path = null;
		String s = IdUtil.randomUUID();
		String name = (String) friendMap.get("name");
		Integer uin = (Integer) friendMap.get("uin");
		if (url.contains("video")) {
			path = "D://114161171/" +name +uin + "/" + s + ".mp4";
		} else {
			path=  "D://114161171/" + name +uin  + "/" + s + ".png";
		}
		boolean exist = FileUtil.exist(path);
		if (!exist) {
			File file = FileUtil.newFile(path);
			long copy = IoUtil.copy(dataInputStream, FileUtil.getOutputStream(file));
			System.out.println(copy);
		}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error("url读取错误："+e);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("io读写异常：" + e);
			return true;
		}
		return true;
	}
}
