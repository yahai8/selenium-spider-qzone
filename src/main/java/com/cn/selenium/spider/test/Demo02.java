package com.cn.selenium.spider.test;

import lombok.Data;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author: MuYaHai
 * Date: 2021/1/13, Time: 14:58
 */
@Data
public class Demo02 {
	private String test2;

	public static void main(String[] args) {
		File file = new File("E:\\fazhiziyuan");
		test2("", file.listFiles());
	}

	public static void test(String rootName, File[] files) {
		for (File file : files) {
			String name1 = file.getName()
					.replaceAll(":", "")
					.replaceAll("：", "")
					.replaceAll("-", "")
					.replaceAll(" ", "")
					.replaceAll("，", "")
					.replaceAll("——", "")
					.replaceAll("（", "")
					.replaceAll("）", "")
					.replaceAll("”", "")
					.replaceAll("“", "")
					.replaceAll("《", "")
					.replaceAll("》", "")
					.replaceAll("-", "")
					.replaceAll(",", "")
					.replaceAll(" ", "");
			String pingYin1 = getPingYin(name1, "");
			File file2 = new File(file.getParent() + File.separator + pingYin1);
			System.out.println(file.getName() + "\t" + pingYin1);
			file.renameTo(file2);
		}
	}


	public static void test2(String rootName, File[] files) {
		for (File file : files) {
			String name1 = file.getName().replaceAll("-", "")
					.replaceAll(" ", "")
					.replaceAll("，", "")
					.replaceAll("——", "")
					.replaceAll("（", "")
					.replaceAll("）", "")
					.replaceAll("”", "")
					.replaceAll("“", "")
					.replaceAll("《", "")
					.replaceAll("》", "")
					.replaceAll("：","")
					.replace("、","");
			String pingYin1 = getPingYin(name1, "");
			File file2 = new File(file.getParent() + File.separator + pingYin1);
			file.renameTo(file2);
			System.out.println(file.getName() + "\t"+ pingYin1 + "\t");

			File[] listFiles = file2.listFiles();
			if (listFiles != null) {
				for (File listFile : listFiles) {
					String name2 = listFile.getName().replaceAll("-", "")
							.replaceAll(" ", "")
							.replaceAll("，", "")
							.replaceAll("——", "")
							.replaceAll("（", "")
							.replaceAll("）", "")
							.replaceAll("”", "")
							.replaceAll("“", "")
							.replaceAll("《", "")
							.replaceAll("》", "")
							.replaceAll("：","")
							.replace("、","");
					String pingYin2 = getPingYin(name2);
					File file3 = new File(listFile.getParent() + File.separator + pingYin2);
					listFile.renameTo(file3);
/*				File[] files1 = file3.listFiles();
				if (files1 != null) {
					for (File file1 : files1) {
						String name3 = file1.getName().replaceAll("-", "")
								.replaceAll(" ", "")
								.replaceAll("，", "")
								.replaceAll("——", "")
								.replaceAll("（", "")
								.replaceAll("）", "")
								.replaceAll("”", "")
								.replaceAll("“", "")
								.replaceAll("《", "")
								.replaceAll("》", "");
						String pingYin3 = getPingYin(name3);
						File file4 = new File(file1.getParent() + File.separator + pingYin3);
						file1.renameTo(file4);
//					System.out.println(pingYin1+"\t"+pingYin2+"\t"+pingYin3+"\t"+pingYin1+"/"+pingYin2+"/"+pingYin3);
						System.out.println(file.getName()+"\t"+listFile.getName()+"\t"+file1.getName()+"\t"+file.getName()+"/"+listFile.getName()+"/"+file1.getName()+"\t"+pingYin1+"\t"+pingYin2+"\t"+pingYin3+"\t"+pingYin1+"/"+pingYin2+"/"+pingYin3);
					}
				}*/
					System.out.println(file.getName() + "\t" + listFile.getName() + "\t" + file.getName() + "/" + listFile.getName() + "\t" + pingYin1 + "\t" + pingYin2 + "\t" + pingYin1 + "/" + pingYin2);

				}
			}
		}
	}


	public static String getPingYin(String inputString) {

		//创建转换对象
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		//转换类型（大写or小写）
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		//定义中文声调的输出格式
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		//定义字符的输出格式
		format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);

		//转换为字节数组
		char[] input = inputString.trim().toCharArray();
		// 用StringBuffer（字符串缓冲）来接收处理的数据
		StringBuffer output = new StringBuffer();

		try {
			for (int i = 0; i < input.length; i++) {
				//判断是否是一个汉子字符
				if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
					output.append(temp[0]);
				} else {
					// 如果不是汉字字符，直接拼接
					output.append(java.lang.Character.toString(input[i]));
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	public static String getPingYin(String text, String separator) {
		char[] chars = text.toCharArray();
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		// 设置大小写
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		// 设置声调表示方法
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		// 设置字母u表示方法
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		String[] s;
		String rs = StringUtils.EMPTY;
		try {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < chars.length; i++) {
				// 判断是否为汉字字符
				if (String.valueOf(chars[i]).matches("[\\u4E00-\\u9FA5]+")) {
					s = PinyinHelper.toHanyuPinyinStringArray(chars[i], format);
					if (s != null) {
						sb.append(s[0]);
						continue;
					}
				}
				sb.append(String.valueOf(chars[i]));
				if ((i + 1 >= chars.length) || String.valueOf(chars[i + 1]).matches("[\\u4E00-\\u9FA5]+")) {
					sb.append(separator);
				}
			}
			rs = sb.substring(0, sb.length());
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return rs;
	}
}

