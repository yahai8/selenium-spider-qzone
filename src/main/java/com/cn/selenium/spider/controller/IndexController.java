package com.cn.selenium.spider.controller;

import com.cn.selenium.spider.entity.QqArticle;
import com.cn.selenium.spider.entity.reponse.Result;
import com.cn.selenium.spider.mq.config.RabbitConfig;
import com.cn.selenium.spider.service.IQqArticleService;
import com.cn.selenium.spider.service.IQqFriendsService;
import com.cn.selenium.spider.service.IQqLogService;
import com.cn.selenium.spider.service.IQqMsgService;
import com.cn.selenium.spider.socket.WebSocket;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: MuYaHai
 * Date: 2020/9/27, Time: 9:52
 */
@RestController
@RequestMapping("/index")
public class IndexController {
	@Resource
	IQqLogService logService;
	@Resource
	IQqMsgService msgService;
	@Resource
	IQqFriendsService friendsService;
	@Resource
	IQqArticleService articleService;
	@Resource
	WebSocket webSocket;
	@Resource
	AmqpTemplate amqpTemplate;


	@GetMapping("/getCount")
	public Result getCount() {
		Map<String, Integer> map = new HashMap<>();
		int logNum = logService.count();
		int msgNum = msgService.count();
		int friendsNum = friendsService.count();
		int articleNum = articleService.count();
		map.put("logNum", logNum);
		map.put("msgNum", msgNum);
		map.put("friendsNum", friendsNum);
		map.put("articleNum", articleNum);
		return Result.SUCCESS(map);
	}

	@PostMapping("/send")
	public Result send(MultipartFile multipartFile,@RequestParam("userName") String userName, @RequestParam("msg") String msg) {
	String	fileSavePath = "D:\\1.ppt";
		String url = "https://yazhuojy-dyyk-oss-cn-hangzhou-aliyuncs-com.oss-cn-hangzhou.aliyuncs.com/yazhuojy-dyyk-oss-cn-hangzhou-aliyuncs-com/resource/1-1laodong.pdf";
		long star = System.currentTimeMillis();
		boolean flag = true;
		File file = new File(fileSavePath);
		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding("utf-8");
		ftpClient.enterLocalActiveMode();
		String fileName = file.getName();
		String filePath = file.getPath();
		if ((fileName.substring(fileName.lastIndexOf(".")).trim().equals(".ppt"))) {
			try {
				ftpClient.connect("182.148.48.236", 23436);
				//登录ftp
				ftpClient.login("ftpuser", "YazhuoJY");
				int reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
				}
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				String serverFileName = "educationBackServer/others/" + fileName;
				OutputStream out = ftpClient.storeFileStream(serverFileName);
				URL url1 = new URL(url);
				URLConnection urlConnection = url1.openConnection();
				long lengthLong = urlConnection.getContentLengthLong();
				InputStream input = urlConnection.getInputStream();
				int available = input.available();
				byte[] byteArray = new byte[4096];
				int read = 0;
				int upload = 0;
				BigDecimal count = new BigDecimal(0);
				while ((read = input.read(byteArray)) != -1) {
					out.write(byteArray, 0, read);
					upload += read;
					BigDecimal bigDecimal = new BigDecimal(upload);
					BigDecimal decimal = new BigDecimal(lengthLong);
					BigDecimal divide = bigDecimal.divide(decimal,2,BigDecimal.ROUND_CEILING);
					System.out.println(divide);
/*					if (!divide.equals(count)) {
						webSocket.sendAllMessage(String.valueOf((i)),userName);
						count = divide;
					}*/
				}
				System.out.println(upload);
				out.close();
				ftpClient.logout();
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			} finally {//最后要关闭
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("上传文件共计耗时：" + (end - star) + "毫秒");
		file.delete();
		return Result.SUCCESS("发送成功！");
	}

	@GetMapping("/sendMq")
	public Result sendMq(@RequestParam("userName") String userName) {
		Map map=	new HashMap<String, Object>();
		map.put("chapter", 1);
		map.put("course", 3);
		QqArticle qqArticle = new QqArticle();
		qqArticle.setName("hahhahahah");
		qqArticle.setContent("testetss");
		map.put("qq", qqArticle);
		amqpTemplate.convertAndSend(RabbitConfig.TEST_QUEUE,map);
		return Result.SUCCESS("发送成功！");
	}
}
