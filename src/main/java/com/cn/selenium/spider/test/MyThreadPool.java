package com.cn.selenium.spider.test;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.concurrent.Task;

import java.util.concurrent.*;

/**
 * @author: MuYaHai
 * Date: 2020/12/31, Time: 11:08
 */
public class MyThreadPool {

	private static ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("test-pool-%d").build();

	private static ThreadPoolExecutor service = new ThreadPoolExecutor(
			4,
			40,
			300L,
			TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(1024),
			nameThreadFactory,
			new ThreadPoolExecutor.AbortPolicy()
	);

	public static ExecutorService getService() {
		service.allowCoreThreadTimeOut(true);
		return service;
	}

	public static void newTask(Runnable runnable) {
		service.execute(runnable);
	}

	public static void shutdown() {
		service.shutdown();
	}

}
