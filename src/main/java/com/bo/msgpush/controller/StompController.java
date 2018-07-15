package com.bo.msgpush.controller;

import java.security.Principal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import com.bo.msgpush.domain.ClientMessage;
import com.bo.msgpush.domain.ServerMessage;

/**
 * @Description stomp客户端消息处理器
 * @Author Bo
 * @Version 2018年7月13日 上午9:26:16
 * @码云 https://gitee.com/LeisureBo
 */
@Controller
public class StompController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${server.port}")
	private String appport;

	@Resource
	private SimpUserRegistry simpUserRegistry;// 用来获取连接的客户端信息

	@Resource
	private SimpMessagingTemplate simpMessagingTemplate;// 用于向消息代理发送消息

	@MessageMapping("/send-notice")
	@SendTo("/topic/notice")
	public ServerMessage notice(ClientMessage clientMessage) {
		logger.info("[port: " + appport + "|online: " + simpUserRegistry.getUserCount() + "] 广播消息：" + clientMessage);
		return new ServerMessage(clientMessage.getFromUserId() + " said: " + clientMessage.getMessage());
	}

	@MessageMapping("/send-msg")
	@SendToUser(destinations = "/topic/msg", broadcast = false)
	public ServerMessage sendToUser(ClientMessage clientMessage, Principal principal) {
		logger.info("[port: " + appport + "|online: " + simpUserRegistry.getUserCount() + "] P2P消息：" + clientMessage);
		// 消息发送到指定用户(因为使用了rabbitmq代理->这里使用/topic前缀以节省代理服务器内存，在简单消息代理中可使用/queue前缀)
		ServerMessage serverMessage = new ServerMessage(clientMessage.getFromUserId() + " said to You: " + clientMessage.getMessage());
		simpMessagingTemplate.convertAndSendToUser(clientMessage.getToUserId().trim(), "/topic/msg", serverMessage);
		return new ServerMessage("You said to " + clientMessage.getToUserId() + ": " + clientMessage.getMessage());
	}

	// 处理 @MessageMapping方法所抛出的异常
	@MessageExceptionHandler
	// 把错误信息发送到发布该消息的认证用户
	@SendToUser(destinations = "/topic/error", broadcast = false)
	public ServerMessage handleExceptions(Exception e, ClientMessage clientMessage) {
		logger.error("Error handling message: {}, exp: {}", clientMessage, e.getMessage());
		return new ServerMessage("System: sorry, send msg error..");
	}
}
