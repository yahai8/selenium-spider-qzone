package com.cn.selenium.spider.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.selenium.spider.entity.QqArticle;
import com.cn.selenium.spider.entity.QqComment;
import com.cn.selenium.spider.entity.QqLog;
import com.cn.selenium.spider.entity.QqSource;
import com.cn.selenium.spider.service.*;
import com.cn.selenium.spider.util.QzoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 9:16
 */
@Slf4j
public class SpiderServiceImpl implements SpiderService {
	public static String userQq = null;

	@Resource
	IQqLogService qqLogService;
	@Resource
	IQqArticleService qqArticleService;
	@Resource
	IQqCommentService qqCommentService;
	@Resource
	IQqSourceService qqSourceService;

	/**
	 * 自动化登录，就是为了获取token、cookie、g_tk等信息
	 * @param qq
	 * @param pwd
	 */
	@Override
	public   void loginQqZone(String qq, String pwd,String driverPath) {
		userQq = qq;
		System.setProperty("webdriver.chrome.driver", driverPath);
		ChromeDriver chromeDriver = new ChromeDriver();
		try {
			chromeDriver.get("https://i.qq.com/");
			String title = chromeDriver.getTitle();
			this.saveAndPush(QqLog.SUCCESS(null,"开始爬取，进入："+title));
			WebElement loginFrame = chromeDriver.findElementById("login_frame");
			chromeDriver.switchTo().frame(loginFrame);
			this.sleepLog(2000);
			this.saveAndPush(QqLog.SUCCESS(null,"切换用户名和密码的登录方式"));
			chromeDriver.findElementById("switcher_plogin").click();
			WebElement username = chromeDriver.findElementById("u");
			sleepLog(2000);
			this.saveAndPush(QqLog.SUCCESS(null,"输入qq："+qq));
			username.sendKeys(qq);
			WebElement password = chromeDriver.findElementById("p");
			sleepLog(2000);
			this.saveAndPush(QqLog.SUCCESS(null,"输入密码："+pwd));
			password.sendKeys(pwd);
			WebElement login = chromeDriver.findElementById("login_button");
			this.saveAndPush(QqLog.SUCCESS(null,"点击登录并停顿5000毫秒"));
			login.click();
			Thread.sleep(5000);
			WebElement tcaptchaIframe = QzoneUtil.isElementExist(chromeDriver,"tcaptcha_iframe");
			if (tcaptchaIframe != null) {
				this.saveAndPush(QqLog.SUCCESS(null,"进行滑块验证码验证，睡眠2000毫秒"));
				Thread.sleep(2000);
				chromeDriver.switchTo().frame(tcaptchaIframe);
				sleepLog(2000);
				WebElement slideBlock = chromeDriver.findElementById("slideBlock");
				sleepLog(2000);
				boolean flag = true;
				int distance = 192-22;
				Actions actions = new Actions(chromeDriver);
				actions.clickAndHold(slideBlock).perform();
				while (flag) {
					List list = QzoneUtil.get_track(distance);
					for (Object i : list) {
						actions.moveByOffset((Integer) i, 0).perform();
						this.saveAndPush(QqLog.SUCCESS(null,"滑块的css:"+slideBlock.getCssValue("left")));
						WebElement guideText = chromeDriver.findElementById("guideText");
						if ("192px".equals(slideBlock.getCssValue("left"))) {
							this.saveAndPush(QqLog.SUCCESS(null,"验证成功"));
							flag = false;
							break;
						}
						if ("拖动下方滑块完成拼图".equals(guideText.getText()) || "请控制拼图块对齐缺口".equals(guideText.getText()) || "这题有点难呢，已为您更换题目".equals(guideText.getText())) {
							System.out.println("重试图形拖动验证码");
							Thread.sleep(2000);
						} else {
							flag = false;
							this.saveAndPush(QqLog.SUCCESS(null,"验证成功"));
							break;
						}
					}
				}
				actions.release(slideBlock).perform();
			}
			Thread.sleep(2000);
			Map cookie = QzoneUtil.get_cookie(chromeDriver);
			this.saveAndPush(QqLog.SUCCESS(null,"开始爬取信息"));
			get_FriendInfo(QzoneUtil.get_g_tk(cookie),QzoneUtil.get_g_qZoneToken(chromeDriver.getPageSource()),cookie);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			chromeDriver.close();
		}
	}

	/**
	 * 爬取好友具体信息，说说内容，图片视频等
	 * @param gtk
	 * @param token
	 * @param cookie
	 * @throws IOException
	 */
	public  void get_FriendInfo(int gtk, String token, Map cookie) throws IOException {
		List<Map> allFriends = get_AllFriends(gtk, token,cookie);
		int count = 0;
		for (Map friend : allFriends) {
			try {
				Integer qq = (Integer) friend.get("uin");
				this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"开始爬取qq："+qq+",姓名："+friend.get("name")+"的好友信息"));
				String url = "https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin="+qq+"&inCharset=utf-8&outCharset=utf-8&hostUin='+str(qq)+'&notice=0&sort=0&pos=0&num=20&cgi_host=http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6&code_version=1&format=jsonp&need_private_comment=1&g_tk="+gtk+"&qzonetoken="+token;
				this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"url地址:"+url));
				String response = QzoneUtil.get_response(url, cookie);
				String sub = StrUtil.sub(response, response.indexOf("(")+1, response.lastIndexOf(")"));
				JSONObject jsonObject = JSONObject.parseObject(sub);
				//总说说内容
				JSONArray msglist = (JSONArray) jsonObject.get("msglist");
				if (msglist != null && msglist.size()-1 > 0) {
					for (int i = 0; i < msglist.size(); i++) {
						JSONObject object = (JSONObject) msglist.get(i);
						QqArticle qqArticle = new QqArticle();
						Object name = object.get("name");
						qqArticle.setName((String) name);
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"发布人：" + name));
						//说说内容
						Object content = object.get("content");
						qqArticle.setContent((String) content);
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"发布内容:"+content));
						//发布说说的时间
						Object createTime = object.get("createTime");
						qqArticle.setCreateTime((String) createTime);
						//时间戳
						Object createdTime = object.get("created_time");
						qqArticle.setQqNum(userQq);
						qqArticleService.save(qqArticle);
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"发布时间："+createTime));
						//评论内容
						JSONArray commentlist = (JSONArray) object.get("commentlist");
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"=======================评论内容开始======================"));
						if (commentlist != null && commentlist.size() > 0) {
							for (int j = 0; j < commentlist.size(); j++) {
								QqComment qqComment = new QqComment();
								qqComment.setArticleId(qqArticle.getId());
								log.info(".............................");
								this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "发布时间：" + createTime));
								JSONObject comment = (JSONObject) commentlist.get(i);
								//评论人姓名
								Object commentName = comment.get("name");
								qqComment.setName((String) name);
								log.info("评论人姓名：" + commentName);
								//评论内容
								Object commentContent = comment.get("content");
								qqComment.setContent((String) commentContent);
								this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "评论内容：" + commentContent));
								//含中文时间
								Object commentTime = comment.get("createTime");
								qqComment.setCreateTime((String) createTime);
								//全数字
								Object commentTime2 = comment.get("createTime2");
								qqComment.setCreateTime2((String) commentTime2);
								//时间戳
								Object commentTime3 = comment.get("create_time");
								qqComment.setCreateTime3((String) commentTime3);
								log.info("评论时间：" + commentTime3);
								//评论人qq
								Object commentQQ = comment.get("uin");
								qqComment.setQqNum((String) commentQQ);
								log.info("评论人qq号码：" + commentQQ);
								qqCommentService.save(qqComment);
							}
						} else {
							this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"暂无评论"));
						}
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"=======================评论内容结束======================"));
						//照片列表
						JSONArray picList = (JSONArray) object.get("pic");
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"=======================下载说说附带照片======================"));
						if (picList != null && picList.size() > 0) {
							for (int j = 0; j < picList.size(); j++) {
								QqSource qqSource = new QqSource();
								JSONObject picElement = (JSONObject) picList.get(j);
								//获取照片
								Object picId = picElement.get("pic_id");
								Object absolutePosition = picElement.get("absolute_position");
								qqSource.setArticleId(qqArticle.getId());
								qqSource.setUrl(url);
								log.info("计数：" + absolutePosition);
								this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "照片地址：" + picId));
								log.info("照片地址：" + picId);
								String path = QzoneUtil.download((String) picId, friend);
								qqSource.setUrlLocal(path);
								qqSourceService.save(qqSource);
							}
						} else {
							this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "未发表照片"));
						}
						//视频
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"=======================下载说说附带视频======================"));
						JSONArray videoList = (JSONArray) object.get("video");
						if (videoList != null && videoList.size() > 0) {
							for (int j = 0; j < videoList.size(); j++) {
								JSONObject video = (JSONObject) videoList.get(j);
								Object url3 = video.get("url3");
								log.info("视频地址：" + url3);
								QzoneUtil.download((String) url3, friend);
							}
						} else {
							this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq),"暂无视频"));
						}
					}
				}
				this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "爬取qq：" + qq + ",姓名：" + friend.get("name") + "的好友信息结束，共爬取" + msglist.size() + "条数据"));
				count += msglist.size();
			} catch (Exception e) {
				//如有异常结束本好友爬取，继续爬取
				e.printStackTrace();
				continue;
			}
		}
		this.saveAndPush(QqLog.SUCCESS(null, "爬取空间好友说说结束，共爬取"+allFriends.size()+"个好友"+ count + "条数据"));
	}


	/**
	 * 获取好友列表，姓名和qq号，如以添加备注信息名字就是备注
	 * @param gtk
	 * @param token
	 * @param cookie
	 * @return
	 */
	public  List<Map> get_AllFriends(int gtk, String token, Map cookie) {
		this.saveAndPush(QqLog.SUCCESS(null,"开始获取好友列表"));
		String url = "https://user.qzone.qq.com/proxy/domain/r.qzone.qq.com/cgi-bin/tfriend/friend_ship_manager.cgi?uin=114161171&do=1&fupdate=1&clean=1&g_tk=" + gtk + "&qzonetoken=" + token;
		try {
			String pageSource = QzoneUtil.get_response(url, cookie);
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
			this.saveAndPush(QqLog.FAIL(null,e.getMessage(),"获取好友列表失败"));
			return null;
		}
	}


	/**
	 * 保存日志并推送
	 * @param qqLog
	 */
	public void saveAndPush(QqLog qqLog) {
		qqLog.setQq(userQq);
		qqLogService.save(qqLog);
		log.info(qqLog.toString());
	}


	/**
	 * 模仿人为操作，睡眠并记录日志
	 * @param time
	 */
	public void sleepLog(Integer time) {
		QqLog qqLog = QqLog.SUCCESS(null, "睡眠" + time + "毫秒");
		qqLog.setQq(userQq);
		qqLogService.save(qqLog);
		log.info(qqLog.toString());
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
}
