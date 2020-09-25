package com.cn.selenium.spider.controller;

import com.cn.selenium.spider.entity.User;
import com.cn.selenium.spider.entity.reponse.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: MuYaHai
 * Date: 2020/9/24, Time: 16:37
 */
@RestController
@RequestMapping("/vue-element-admin/user")
public class LoginController {

	@PostMapping("/login")
	public Result login(@RequestBody User user) {
		HashMap<String, String> hashMap = new HashMap<>();
		if ("admin".equals(user.getUsername()) && "111111".equals(user.getPassword())) {
			hashMap.put("token", "admin-token");
			return Result.SUCCESS(200, hashMap);
		} else {
			return Result.FAIL("登陆失败！", 50008);
		}
	}

	@GetMapping("/info")
	public Result info(@RequestParam(name = "token") String token) {
		HashMap<String, Object> hashMap = new HashMap<>();
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("admin");
		if ("admin-token".equals(token) ) {
			hashMap.put("roles", arrayList);
			hashMap.put("introduction", "I am a super administrator");
			hashMap.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
			hashMap.put("name", "Super Admin");
			return Result.SUCCESS(200, hashMap);
		} else {
			return Result.FAIL("认证失败！", 60204);
		}
	}
}
