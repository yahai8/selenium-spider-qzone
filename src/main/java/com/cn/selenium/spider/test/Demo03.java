package com.cn.selenium.spider.test;

import cn.hutool.core.lang.UUID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: MuYaHai
 * Date: 2021/1/19, Time: 11:14
 */
public class Demo03 {
	public static void main(String[] args) throws IOException {
		List<String> list = new ArrayList<>();
		for (int i = 7; i < 8; i++) {
			String format = " INSERT INTO `Yazhuo_AR`.`DataUser`(`ID`, `UserName`, `PassWord`, `MailAddr`, `ActionCode`, `LastTime`, `Name`, `Sex`, `SchoolID`, `GradeID`, `ClassID`, `Character`, `Description`, `Permission`, `HeadIcon`, `MailTime`, `IsDelete`) \n" +
					" VALUES ('%s', '%s', '81dc9bdb52d04dc20036dbd8313ed055', 'muyahai@163.com', '97E66A78-043A-CBE7-E11A-2318BB74EE87', '0', '%s', 2, 'ACFA8BE1-6128-3542-4672-3422E903B4D7', '0', '0', 3, NULL, '', NULL, '', 0);\n";
			String id = UUID.randomUUID().toString();
			String name = "yazhuoar"+i;
			list.add(name);
			String format1 = String.format(format,id , name, name);
			System.out.println(format1);
		}
		for (String s : list) {
			System.out.println(s);
		}
	}
}
