package com.cn.selenium.spider.config;

import com.cn.selenium.spider.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * Created by macro on 2018/4/26.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.cn.selenium.spider.controller")
                .title("爬取QQ空间")
                .description("QQ空间爬取后台相关接口文档")
                .contactName("MuYaHai")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}
