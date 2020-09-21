package com.cn.selenium.spider.test;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: MuYaHai
 * Date: 2020/9/16, Time: 10:17
 */
public class SeleniumLearn {
	public static void main(String[] args) {
		List track = get_track(192-22);

		System.setProperty("webdriver.chrome.driver", "c:/driver/chromedriver.exe");
		ChromeDriver chromeDriver = new ChromeDriver();
		try {
			chromeDriver.get("https://mail.qq.com/");
			String title = chromeDriver.getTitle();
			System.out.println(title + "...........");
			System.out.println("开始获取登录iframe...........");
			WebElement loginFrame = chromeDriver.findElementById("login_frame");
			chromeDriver.switchTo().frame(loginFrame);
			System.out.println("切换到qq登录...........");
//			chromeDriver.findElementById("switcher_plogin").click();
			WebElement username = chromeDriver.findElementById("u");
			System.out.println("输入用户名...........");
			Thread.sleep(2000);
			username.sendKeys("1317649641");
			WebElement password = chromeDriver.findElementById("p");
			System.out.println("输入密码...........");
			Thread.sleep(2000);
			password.sendKeys("qwerty1314.520");
			WebElement login = chromeDriver.findElementById("login_button");
			System.out.println("点击登录...........");
			Thread.sleep(5000);
			login.click();
			System.out.println("图形验证码...........");
			Thread.sleep(2000);
			WebElement tcaptchaIframe = chromeDriver.findElementById("tcaptcha_iframe");
			Thread.sleep(5000);
			chromeDriver.switchTo().frame(tcaptchaIframe);
			Thread.sleep(5000);
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
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {

//			chromeDriver.close();
		}
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
}
