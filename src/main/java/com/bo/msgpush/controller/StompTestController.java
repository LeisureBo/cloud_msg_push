package com.bo.msgpush.controller;

import java.security.Principal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import com.bo.msgpush.domain.ClientMessage;

/**
 * @Description stomp客户端消息处理器
 * @Author Bo
 * @Version 2018年7月13日 上午9:26:16
 * @码云 https://gitee.com/LeisureBo
 */
@Controller
public class StompTestController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${server.port}")
	private String appport;
	
	private static final String USER_ROUTING_KEY_PREFIX = "msg.";
	
	private static final String TOPIC_ROUTING_KEY = "notice";
	
	private static final String EXCHANGE_PREFIX = "/topic/";
	
	@Resource
	private SimpUserRegistry simpUserRegistry;// 用来获取连接的客户端信息

	@Resource
	private SimpMessagingTemplate simpMessagingTemplate;// 用于向消息代理发送消息

	@MessageMapping("/send-notice")
	@SendTo("/topic/notice")
	public ClientMessage notice(ClientMessage clientMessage) {
		logger.info("[port: " + appport + "|online: " + simpUserRegistry.getUserCount() + "] 广播消息：" + clientMessage);
		return new ClientMessage(clientMessage.getFromUserId() + " said: " + clientMessage.getMessage());
	}

	@MessageMapping("/send-msg")
	@SendToUser(destinations = "/topic/msg", broadcast = false)
	public ClientMessage sendToUser(ClientMessage clientMessage, Principal principal) {
		logger.info("[port: " + appport + "|online: " + simpUserRegistry.getUserCount() + "] P2P消息：" + clientMessage);
		// 消息发送到指定用户(因为使用了rabbitmq代理->这里使用/topic前缀以节省代理服务器创建queue的内存，在简单消息代理中可使用/queue前缀)
		ClientMessage serverMessage = new ClientMessage(clientMessage.getFromUserId() + " said to You: " + clientMessage.getMessage());
		simpMessagingTemplate.convertAndSendToUser(clientMessage.getToUserId().trim(), "/topic/msg", serverMessage);
		return new ClientMessage("You said to " + clientMessage.getToUserId() + ": " + clientMessage.getMessage());
	}

	// 处理 @MessageMapping方法所抛出的异常
	@MessageExceptionHandler
	// 把错误信息发送到发布该消息的认证用户(需要订阅"/user"前缀)
	@SendToUser(destinations = {"/topic/error"}, broadcast = false)
	public ClientMessage handleExceptions(Exception e, ClientMessage clientMessage) {
		logger.error("Error handling message: {}, exp: {}", clientMessage, e.getMessage());
		ClientMessage serverMessage = new ClientMessage("System: sorry, send msg error..");
		simpMessagingTemplate.convertAndSend(EXCHANGE_PREFIX + USER_ROUTING_KEY_PREFIX + clientMessage.getFromUserId(), serverMessage);
		return serverMessage;
	}
	
	@MessageMapping("/send-topq")
	public void sendToPersistQueue(ClientMessage clientMessage, Principal principal, MessageHeaders messageHeaders) {
		logger.info("[port: " + appport + "|online: " + simpUserRegistry.getUserCount() + "] P2P消息：" + clientMessage);
		ClientMessage serverMessage = new ClientMessage(clientMessage.getFromUserId() + " said to You: " + clientMessage.getMessage());
		// 发送消息到自定义持久化队列(通过simpMessagingTemplate发送的消息并非持久化消息)
		// 这里将所有自定义持久化队列都通过routingkey绑定到了topic交换机，所以可通过"/topic/{routingkey}"前缀发送消息到指定目的地
		// 1.发送消息到指定用户: 只需指定标识目标用户的routingkey即可，该routingkey是在用户订阅时绑定用户个人私有消息队列到topic交换机声明的
		// simpMessagingTemplate.convertAndSend("/topic/{user_routingkey}", serverMessage);
		// 2.发送消息到全部用户: 只需指定所有用户均绑定的routingkey，该routingkey是在用户订阅时绑定用户个人系统消息队列到topic交换机声明的
		// simpMessagingTemplate.convertAndSend("/topic/{sys_routingkey}", serverMessage);
		// 3.或者通过exchange前缀发送到自定义交换机
		// simpMessagingTemplate.convertAndSend("/exchange/{exchange_name}/{routing_key}", serverMessage);
		simpMessagingTemplate.convertAndSend(EXCHANGE_PREFIX + USER_ROUTING_KEY_PREFIX + clientMessage.getToUserId(), serverMessage);
	}
}
