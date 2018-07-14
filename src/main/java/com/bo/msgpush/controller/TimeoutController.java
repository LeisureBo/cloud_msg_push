package com.bo.msgpush.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description timeout测试
 * @Author Bo
 * @Version 2018年7月11日　下午6:41:40
 * @码云 https://gitee.com/LeisureBo
 */
@RestController
public class TimeoutController {
	
	@Value("${server.port}")
	private String port;
	
	@RequestMapping("/timeout")
	public String timeout() {
		try {
			Thread.sleep(32000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "timeout";
	}
	
	@RequestMapping("/from")
	public String from() {
		return "I come from " + port;
	}
	
}
