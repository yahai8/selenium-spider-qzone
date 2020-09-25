package com.cn.selenium.spider.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.selenium.spider.entity.*;
import com.cn.selenium.spider.mq.RabbitMqSender;
import com.cn.selenium.spider.mq.param.MqParam;
import com.cn.selenium.spider.service.*;
import com.cn.selenium.spider.socket.WebSocket;
import com.cn.selenium.spider.util.QzoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Service;

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
@Service
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
	@Resource
	IQqFriendsService friendsService;
	@Resource
	IQqPhotoAlbumService photoAlbumService;
	@Resource
	IQqPhotoService photoService;
	@Resource
	RabbitMqSender rabbitMqSender;
	@Resource
	IQqMsgService msgService;
	@Resource
	WebSocket webSocket;

	/**
	 * 自动化登录，就是为了获取token、cookie、g_tk等信息
	 *
	 * @param qq
	 * @param pwd
	 */
	@Override
	public void loginQqZone(String qq, String pwd, String driverPath) {
		userQq = qq;
		System.setProperty("webdriver.chrome.driver", driverPath);
		ChromeDriver chromeDriver = new ChromeDriver();
		try {
			chromeDriver.get("https://i.qq.com/");
			String title = chromeDriver.getTitle();
			this.saveAndPush(QqLog.SUCCESS(null, "开始爬取，进入：" + title));
			WebElement loginFrame = chromeDriver.findElementById("login_frame");
			chromeDriver.switchTo().frame(loginFrame);
			this.sleepLog(2000);
			this.saveAndPush(QqLog.SUCCESS(null, "切换用户名和密码的登录方式"));
			chromeDriver.findElementById("switcher_plogin").click();
			WebElement username = chromeDriver.findElementById("u");
			sleepLog(2000);
			this.saveAndPush(QqLog.SUCCESS(null, "输入qq：" + qq));
			username.sendKeys(qq);
			WebElement password = chromeDriver.findElementById("p");
			sleepLog(2000);
			this.saveAndPush(QqLog.SUCCESS(null, "输入密码：" + pwd));
			password.sendKeys(pwd);
			Thread.sleep(5000);
			WebElement login = chromeDriver.findElementById("login_button");
			this.saveAndPush(QqLog.SUCCESS(null, "点击登录并停顿5000毫秒"));
			login.click();
			Thread.sleep(5000);
			WebElement tcaptchaIframe = QzoneUtil.isElementExist(chromeDriver, "tcaptcha_iframe");
			if (tcaptchaIframe != null) {
				this.saveAndPush(QqLog.SUCCESS(null, "进行滑块验证码验证，睡眠2000毫秒"));
				Thread.sleep(2000);
				chromeDriver.switchTo().frame(tcaptchaIframe);
				sleepLog(2000);
				WebElement slideBlock = chromeDriver.findElementById("slideBlock");
				sleepLog(2000);
				boolean flag = true;
				int distance = 192 - 22;
				Actions actions = new Actions(chromeDriver);
				actions.clickAndHold(slideBlock).perform();
				while (flag) {
					List list = QzoneUtil.get_track(distance);
					for (Object i : list) {
						actions.moveByOffset((Integer) i, 0).perform();
						this.saveAndPush(QqLog.SUCCESS(null, "滑块的css:" + slideBlock.getCssValue("left")));
						WebElement guideText = chromeDriver.findElementById("guideText");
						if ("192px".equals(slideBlock.getCssValue("left"))) {
							this.saveAndPush(QqLog.SUCCESS(null, "验证成功"));
							flag = false;
							break;
						}
						if ("拖动下方滑块完成拼图".equals(guideText.getText()) || "请控制拼图块对齐缺口".equals(guideText.getText()) || "这题有点难呢，已为您更换题目".equals(guideText.getText())) {
							System.out.println("重试图形拖动验证码");
							Thread.sleep(2000);
						} else {
							flag = false;
							this.saveAndPush(QqLog.SUCCESS(null, "验证成功"));
							break;
						}
					}
				}
				actions.release(slideBlock).perform();
			}
			Thread.sleep(2000);
			Map cookie = QzoneUtil.get_cookie(chromeDriver);
			this.saveAndPush(QqLog.SUCCESS(null, "开始爬取信息"));
			get_FriendInfo(QzoneUtil.get_g_tk(cookie), QzoneUtil.get_g_qZoneToken(chromeDriver.getPageSource()), cookie);
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
	 *
	 * @param gtk
	 * @param token
	 * @param cookie
	 * @throws IOException
	 */
	public void get_FriendInfo(int gtk, String token, Map cookie) throws IOException {
		List<Map> allFriends = get_AllFriends(gtk, token, cookie);
		int count = 0;
		MqParam mqParam = new MqParam();
		mqParam.setCookieMap(cookie);
		mqParam.setGtk(gtk);
		mqParam.setToken(token);
		mqParam.setQq(userQq);
		for (Map friend : allFriends) {
			mqParam.setFriendMap(friend);
			rabbitMqSender.sendToGetInfo(JSON.toJSONString(mqParam));
		}
		this.saveAndPush(QqLog.SUCCESS(null, "爬取空间好友说说结束，共爬取" + allFriends.size() + "个好友" + count + "条数据"));
	}

	@Override
	public int getFriendInfo(MqParam mqParam) {
		int count = 0;
		try {

//			Thread.sleep(4000);
			Map friend = mqParam.getFriendMap();
			String qq = String.valueOf(friend.get("uin"));
			String friendName = String.valueOf(friend.get("name"));
			QqFriends qqFriends = new QqFriends();
			qqFriends.setCreateTime(new Date());
			qqFriends.setFriendQq(qq);
			qqFriends.setFriendName(friendName);
			friendsService.save(qqFriends);
			this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "开始爬取qq：" + qq + ",姓名：" + friendName + "的好友信息"));
			String url = "https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin=" + qq + "&inCharset=utf-8&outCharset=utf-8&hostUin='+str(qq)+'&notice=0&sort=0&pos=0&num=20&cgi_host=http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6&code_version=1&format=jsonp&need_private_comment=1&g_tk=" + mqParam.getGtk() + "&qzonetoken=" + mqParam.getToken();
			this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "url地址:" + url));
			String response = QzoneUtil.get_response(url, mqParam.getCookieMap());
			String sub = StrUtil.sub(response, response.indexOf("(") + 1, response.lastIndexOf(")"));
			JSONObject jsonObject = JSONObject.parseObject(sub);
			String message = String.valueOf(jsonObject.get("message"));
			if (message != null && !"".equals(message)) {
				return count;
			}
			rabbitMqSender.sendGetMsg(JSON.toJSONString(mqParam));
			rabbitMqSender.sendToPhoto(JSON.toJSONString(mqParam));
			Object total = jsonObject.get("total");
			Integer value = Integer.valueOf(String.valueOf(total));
			int page = 0;
			if (value % 20 == 0) {
				page = value / 20;
			} else {
				page = value / 20 + 1;
			}
			this.saveAndPush(QqLog.SUCCESS(qq, "一共有" + value + "条说说"));
			//总说说内容
			for (int a = 0; a < page; a++) {
//				Thread.sleep(4000);
				int pos = a * 20;
				url = "https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin=" + qq + "&inCharset=utf-8&outCharset=utf-8&hostUin=" + qq + "&notice=0&sort=0&pos=" + pos + "&num=20&cgi_host=http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6&code_version=1&format=jsonp&need_private_comment=1&g_tk=" + mqParam.getGtk() + "&qzonetoken=" + mqParam.getToken();
				this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "url地址:" + url));
				response = QzoneUtil.get_response(url, mqParam.getCookieMap());
				sub = StrUtil.sub(response, response.indexOf("(") + 1, response.lastIndexOf(")"));
				jsonObject = JSONObject.parseObject(sub);
				JSONArray msglist = (JSONArray) jsonObject.get("msglist");
				if (msglist != null && msglist.size() - 1 > 0) {
					for (int i = 0; i < msglist.size(); i++) {
//						Thread.sleep(2000);
						JSONObject object = (JSONObject) msglist.get(i);
						QqArticle qqArticle = new QqArticle();
						Object name = object.get("name");
						qqArticle.setName((String) name);
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "发布人：" + name));
						//说说内容
						Object content = object.get("content");
						qqArticle.setContent((String) content);
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "发布内容:" + content));
						//发布说说的时间
						Object createTime = object.get("createTime");
						qqArticle.setCreateTime((String) createTime);
						//时间戳
						Object createdTime = object.get("created_time");
						qqArticle.setQqNum(userQq);
						qqArticle.setFriendQq(qq);
						qqArticleService.save(qqArticle);
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "发布时间：" + createTime));
//						Thread.sleep(2000);
						//评论内容
						JSONArray commentlist = (JSONArray) object.get("commentlist");
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "=======================评论内容开始======================"));
						if (commentlist != null && commentlist.size() > 0) {
							for (int j = 0; j < commentlist.size(); j++) {
//								Thread.sleep(2000);
								QqComment qqComment = new QqComment();
								qqComment.setArticleId(qqArticle.getId());
								log.info(".............................");
								this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "发布时间：" + createTime));
								JSONObject comment = (JSONObject) commentlist.get(j);
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
								qqComment.setCreateTime3(String.valueOf(commentTime3));
								log.info("评论时间：" + commentTime3);
								//评论人qq
								Object commentQQ = comment.get("uin");
								qqComment.setQqNum(String.valueOf(commentQQ));
								log.info("评论人qq号码：" + commentQQ);
								qqCommentService.save(qqComment);
							}
						} else {
							this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "暂无评论"));
						}
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "=======================评论内容结束======================"));
						//照片列表
						JSONArray picList = (JSONArray) object.get("pic");
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "=======================下载说说附带照片======================"));
						if (picList != null && picList.size() > 0) {
							for (int j = 0; j < picList.size(); j++) {
//								Thread.sleep(2000);
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
								String path = QzoneUtil.download(String.valueOf(picId), friend);
								qqSource.setUrlLocal(path);
								qqSourceService.save(qqSource);
							}
						} else {
							this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "未发表照片"));
						}
						//视频
						this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "=======================下载说说附带视频======================"));
						JSONArray videoList = (JSONArray) object.get("video");
						if (videoList != null && videoList.size() > 0) {
							for (int j = 0; j < videoList.size(); j++) {
//								Thread.sleep(2000);
								JSONObject video = (JSONObject) videoList.get(j);
								Object url3 = video.get("url3");
								log.info("视频地址：" + url3);
								QzoneUtil.download(String.valueOf(url3), friend);
							}
						} else {
							this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "暂无视频"));
						}
					}
					this.saveAndPush(QqLog.SUCCESS(String.valueOf(qq), "爬取qq：" + qq + ",姓名：" + friend.get("name") + "的好友信息结束，共爬取" + msglist.size() + "条数据"));
					count += msglist.size();
				}
			}
		} catch (Exception e) {
			//如有异常结束本好友爬取，继续爬取
			e.printStackTrace();
			return count;
		}
		return count;
	}


	/**
	 * 获取好友列表，姓名和qq号，如以添加备注信息名字就是备注
	 *
	 * @param gtk
	 * @param token
	 * @param cookie
	 * @return
	 */
	public List<Map> get_AllFriends(int gtk, String token, Map cookie) {
		this.saveAndPush(QqLog.SUCCESS(null, "开始获取好友列表"));
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
			JSONArray jsonArray = JSON.parseArray(suffix + "]");
			cn.hutool.json.JSONArray jsonArray1 = JSONUtil.parseArray(suffix + "]");
			List<Map> mapList = JSONUtil.toList(jsonArray1, Map.class);
			return mapList;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("获取好友列表失败！");
			this.saveAndPush(QqLog.FAIL(null, e.getMessage(), "获取好友列表失败"));
			return null;
		}
	}


	/**
	 * 保存日志并推送
	 *
	 * @param qqLog
	 */
	public void saveAndPush(QqLog qqLog) {
		qqLog.setQq(userQq);
		qqLogService.save(qqLog);
		webSocket.sendAllMessage(qqLog.toString());
		log.info(qqLog.toString());
	}


	/**
	 * 模仿人为操作，睡眠并记录日志
	 *
	 * @param time
	 */
	public void sleepLog(Integer time) {
		QqLog qqLog = QqLog.SUCCESS(null, "睡眠" + time + "毫秒");
		qqLog.setQq(userQq);
		qqLogService.save(qqLog);
		webSocket.sendAllMessage(qqLog.toString());
		log.info(qqLog.toString());
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}


	/**
	 * 爬取好友相册
	 * @param gtk
	 * @param friendMap
	 * @param qq
	 * @param cookie
	 */
	@Override
	public  void get_photo(int gtk, Map friendMap, String qq, Map cookie){
		try {
			String friendName = (String) friendMap.get("name");
			Object friendQq = friendMap.get("uin");
			String url = "https://user.qzone.qq.com/proxy/domain/photo.qzone.qq.com/fcgi-bin/fcg_list_album_v3?g_tk="+gtk+"&callback=shine0_Callback&t=469158111&hostUin="+friendQq+"&uin="+qq+"&appid=4&inCharset=utf-8&outCharset=utf-8&source=qzone&plat=qzone&format=jsonp&notice=0&filter=1&handset=4&pageNumModeSort=40&pageNumModeClass=15&needUserInfo=1&idcNum=4&callbackFun=shine0&_=1600913159677";
			String albumText = QzoneUtil.get_response(url, cookie);
			//截取字符串获取正确的json数据
			String sub = StrUtil.sub(albumText, albumText.indexOf("(")+1, albumText.lastIndexOf(")"));
			JSONObject albumJson = JSON.parseObject(sub);
			JSONObject data = (JSONObject) albumJson.get("data");
			//得到相册的json数据
			JSONArray albumListModeSort = (JSONArray) data.get("albumListModeSort");
			String message = String.valueOf(albumJson.get("message"));
			if (message != null && !"".equals(message)) {
				return;
			}
			//遍历相册
			if (albumListModeSort != null && albumListModeSort.size() > 0) {
				for (int i = 0; i < albumListModeSort.size(); i++) {
					JSONObject album = (JSONObject) albumListModeSort.get(i);
					Object topicId = album.get("id");
					//拿到相册名，如果相册名为空就拿描述，两者必定有一个值
					String name = String.valueOf(album.get("name"));
					if (name == null || "".equals(name)) {
						name = String.valueOf(album.get("desc"));
					}
					//此相册的照片数量
					Integer total = Integer.valueOf(String.valueOf(album.get("total")));
					if (total == null) {
						total=0;
					}
					int pageStart = 0;
					int pageNum = Integer.valueOf(total);
					//获取照片的json数据
					String url2="https://h5.qzone.qq.com/proxy/domain/photo.qzone.qq.com/fcgi-bin/cgi_list_photo?g_tk="+gtk+"&callback=shine0_Callback&t=952444063&mode=0&idcNum=4&hostUin="+friendQq+"&topicId="+topicId+"&noTopic=0&uin="+qq+"&pageStart="+pageStart+"&pageNum="+pageNum+"&skipCmtCount=0&singleurl=1&batchId=&notice=0&appid=4&inCharset=utf-8&outCharset=utf-8&source=qzone&plat=qzone&outstyle=json&format=jsonp&json_esc=1&question=&answer=&callbackFun=shine0&_=1551790719497";
					String photoText = QzoneUtil.get_response(url2, cookie);
					//截取获取正确的json数据
					String sub2 = StrUtil.sub(photoText, photoText.indexOf("(")+1, photoText.lastIndexOf(")"));
					JSONObject jsonObject2 = JSONObject.parseObject(sub2);
					JSONObject data2 = (JSONObject) jsonObject2.get("data");
					JSONArray  photoList = (JSONArray) data2.get("photoList");
					//存库
					QqPhotoAlbum qqPhotoAlbum = new QqPhotoAlbum();
					qqPhotoAlbum.setAlbumName(name);
					qqPhotoAlbum.setCreateTime(new Date());
					qqPhotoAlbum.setAlbumDesc(String.valueOf(album.get("desc")));
					qqPhotoAlbum.setPreUrl(String.valueOf(album.get("pre")));
					String pathPre = QzoneUtil.download(String.valueOf(album.get("pre")), friendMap);
					qqPhotoAlbum.setLocalUrl(pathPre);
					qqPhotoAlbum.setTotal(String.valueOf(total));
					qqPhotoAlbum.setFriendName(friendName);
					qqPhotoAlbum.setFriendQq(String.valueOf(friendQq));
					//这里拿到的系统毫秒值有问题
					Integer createtime = Integer.valueOf(String.valueOf(album.get("createtime")));
					Date date = new Date();
					date.setTime(createtime);
					qqPhotoAlbum.setUploadTime(date);
					photoAlbumService.save(qqPhotoAlbum);
					//遍历照片列表
					if (photoList.size() > 0 && photoList != null) {
						for (int j = 0; j < photoList.size(); j++) {
							JSONObject photo = (JSONObject) photoList.get(j);
							Object url1 = photo.get("url");
							String path = name + "-" + UUID.randomUUID();
							Object desc = photo.get("desc");
							Object uploadtime = photo.get("uploadtime");
							//存库
							QqPhoto qqPhoto = new QqPhoto();
							qqPhoto.setCreateTime(new Date());
							qqPhoto.setPhotoDesc(String.valueOf(desc));
							qqPhoto.setName(name);
							qqPhoto.setPhotoAlbum(name);
							qqPhoto.setPhotoAlbumId(qqPhotoAlbum.getId());
							qqPhoto.setUploadTime(String.valueOf(uploadtime));
							qqPhoto.setUrl(String.valueOf(url1));
							String localUrl = QzoneUtil.download(String.valueOf(url1), friendMap);
							qqPhoto.setLocalUrl(localUrl);
							photoService.save(qqPhoto);
						}
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@Override
	public  void getMsg(MqParam mqParam) {
		try {
			this.saveAndPush(QqLog.SUCCESS(String.valueOf(mqParam.getFriendMap().get("uin")),"开始爬取留言信息"));
			String url ="https://user.qzone.qq.com/proxy/domain/m.qzone.qq.com/cgi-bin/new/get_msgb?uin="+mqParam.getQq()+"&hostUin="+mqParam.getFriendMap().get("uin")+"&start=0&s=0.4110685624024064&format=jsonp&num=10&inCharset=utf-8&outCharset=utf-8&g_tk="+mqParam.getGtk()+"&qzonetoken="+mqParam.getToken()+"&g_tk="+mqParam.getGtk();
			String text = QzoneUtil.get_response(url, mqParam.getCookieMap());
			String sub = StrUtil.sub(text, text.indexOf("(")+1, text.lastIndexOf(")"));
			JSONObject jsonObject = JSON.parseObject(sub);
			JSONObject data = (JSONObject) jsonObject.get("data");
			String value = String.valueOf(data.get("total"));
			int total = 0;
			String message = String.valueOf(jsonObject.get("message"));
			if (message != null && !"".equals(message)) {
				return;
			}
			if (value != null) {
				total = Integer.valueOf(value);
			}
			int page = 0;
			if (total % 100 == 0) {
				page = total / 100;
			} else {
				page = total / 100 + 1;
			}
			for (int i = 0; i < page; i++) {
//				this.sleepLog(2000);
				int pageStart = i * 100;
				url ="https://user.qzone.qq.com/proxy/domain/m.qzone.qq.com/cgi-bin/new/get_msgb?uin="+mqParam.getQq()+"&hostUin="+mqParam.getFriendMap().get("uin")+"&start="+pageStart+"&s=0.4110685624024064&format=jsonp&num=100&inCharset=utf-8&outCharset=utf-8&g_tk="+mqParam.getGtk()+"&qzonetoken="+mqParam.getToken()+"&g_tk="+mqParam.getGtk();
				String text2 = QzoneUtil.get_response(url, mqParam.getCookieMap());
				String sub2 = StrUtil.sub(text2, text2.indexOf("(")+1, text2.lastIndexOf(")"));
				JSONObject jsonObject2 = JSON.parseObject(sub);
				JSONObject data2 = (JSONObject) jsonObject.get("data");
				JSONArray commentList = (JSONArray) data.get("commentList");
				if (commentList.size() > 0 && commentList != null) {
					for (int j = 0; j < commentList.size(); j++) {
						JSONObject obj = (JSONObject) commentList.get(j);
						Object pubtime = obj.get("pubtime");
						Object nickname = obj.get("nickname");
						Object ubbContent = obj.get("ubbContent");
						QqMsg qqMsg = new QqMsg();
						qqMsg.setCreateTime(new Date());
						qqMsg.setName(String.valueOf(mqParam.getFriendMap().get("name")));
						qqMsg.setNickname(String.valueOf(nickname));
						qqMsg.setPubTime(String.valueOf(pubtime));
						qqMsg.setQq(String.valueOf(mqParam.getFriendMap().get("uin")));
						qqMsg.setUbbContent(String.valueOf(ubbContent));
						msgService.save(qqMsg);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
