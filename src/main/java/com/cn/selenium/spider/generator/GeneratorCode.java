package com.cn.selenium.spider.generator;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author: MuYaHai
 * Date: 2020/9/18, Time: 16:37
 */
public class GeneratorCode {

	public static void main(String[] args) {
		AutoGenerator mpg = new AutoGenerator();
		String projectPath = System.getProperty("user.dir")+"/selenium";
		GlobalConfig globalConfig = new GlobalConfig();
		globalConfig.setOutputDir(projectPath + "/src/main/java");
		globalConfig.setAuthor("MuYaHai");
		globalConfig.setOpen(false);
		mpg.setGlobalConfig(globalConfig);

		DataSourceConfig dataSourceConfig = new DataSourceConfig();
		dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/qzone_spider?serverTimezone=GMT%2B8&useUnicode=true&&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true");
		dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
		dataSourceConfig.setUsername("root");
		dataSourceConfig.setPassword("root");
		mpg.setDataSource(dataSourceConfig);

		PackageConfig packageConfig = new PackageConfig();
		packageConfig.setModuleName(scanner("模块名"));
		packageConfig.setParent("com.cn.selenium");
		mpg.setPackageInfo(packageConfig);

		InjectionConfig injectionConfig = new InjectionConfig() {
			@Override
			public void initMap() {

			}
		};

		String templatePath = "/templates/mapper.xml.ftl";


		List<FileOutConfig> focList = new ArrayList<>();
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				return projectPath + "/src/main/resources/mapper/" + packageConfig.getModuleName() + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
			}
		});


		injectionConfig.setFileOutConfigList(focList);
		mpg.setCfg(injectionConfig);

		TemplateConfig templateConfig = new TemplateConfig();
		templateConfig.setXml(null);
		mpg.setTemplate(templateConfig);

		StrategyConfig strategyConfig = new StrategyConfig();
		strategyConfig.setNaming(NamingStrategy.underline_to_camel);
		strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
		strategyConfig.setEntityLombokModel(true);
		strategyConfig.setRestControllerStyle(true);
		strategyConfig.setSuperEntityColumns("id");
		strategyConfig.setInclude(scanner("表名，多个英文逗号分割").split(","));
		strategyConfig.setControllerMappingHyphenStyle(true);
		strategyConfig.setTablePrefix(packageConfig.getModuleName() + "_");
		mpg.setStrategy(strategyConfig);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.execute();
	}

	public static String scanner(String tip) {
		Scanner scanner = new Scanner(System.in);
		StringBuilder help = new StringBuilder();
		help.append("请输入" + tip + "：");
		System.out.println(help.toString());
		if (scanner.hasNext()) {
			String ipt = scanner.next();
			if (StringUtils.isNotBlank(ipt)) {
				return ipt;
			}
		}
		throw new MybatisPlusException("请输入正确的" + tip + "！");
	}
}
