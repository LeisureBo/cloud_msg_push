package com.bo.msgpush.stomp.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.connection.Connection;

/**
 * @Description 客户端rabbitmq连接缓存
 * @Author Bo
 * @Version 2018年7月17日　下午8:21:38
 * @码云 https://gitee.com/LeisureBo
 */
public class ClientRabbitMqConnHolder {
	
	private final Map<String, Connection> connCache = new ConcurrentHashMap<>();
	
	
	public void putConn(String userId, Connection connection) {
		
	}
	
	
}
