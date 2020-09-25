package com.cn.selenium.spider.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.selenium.spider.entity.QqComment;
import com.cn.selenium.spider.entity.QqLog;
import com.cn.selenium.spider.entity.reponse.Result;
import com.cn.selenium.spider.service.IQqLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author MuYaHai
 * @since 2020-09-21
 */
@Slf4j
@RestController
@RequestMapping("/log")
public class QqLogController {
	@Resource
	IQqLogService logService;

	/**
	 * 根据条件查询，无条件也可用
	 * @param qqLog
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/list")
	public Result list(QqLog qqLog,
					   @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
					   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		try {
			return Result.SUCCESS(logService.page(new Page<>(pageNum, pageSize), new QueryWrapper<>(qqLog)));
		} catch (Exception e) {
			log.error("根据条件查询所有报错：{}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 根据id删除
	 * @param id
	 * @return
	 */
	@GetMapping("/delete/{id}")
	public Result delete(@PathVariable(name = "id") Integer id) {
		try {
			boolean remove = logService.removeById(id);
			return Result.SUCCESS("删除成功！");
		} catch (Exception e) {
			log.error("删除说说失败：{}", e.getMessage(), e);
			return Result.FAIL("删除失败，请稍后再试！");
		}
	}


	/**
	 * 根据id删除
	 * @param ids
	 * @return
	 */
	@GetMapping("/deleteBatch")
	public Result deleteBatch(@RequestParam(name = "ids") List<String> ids) {
		try {
			logService.removeByIds(ids);
			return Result.SUCCESS("批量删除成功！");
		} catch (Exception e) {
			log.error("批量删除失败,请稍后再试：{}",e.getMessage(),e);
			return Result.FAIL("批量删除失败，请稍后再试！");
		}
	}

	/**
	 * 更新
	 * @param qqLog
	 * @return
	 */
	@PostMapping("/update")
	public Result update(@RequestBody QqLog qqLog) {
		try {
			logService.updateById(qqLog);
			return Result.SUCCESS("更新成功！");
		} catch (Exception e) {
			log.error("更新失败，请稍后再试:{}", e.getMessage(), e);
			return Result.FAIL("更新失败请稍后再试！");
		}
	}
}
