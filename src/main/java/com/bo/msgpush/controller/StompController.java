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
 * @Description 
 * @Author Bo
 * @Version 2018年7月13日　上午9:26:16
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
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@MessageMapping("/send-notice")
	@SendTo("/topic/notice")
	public ServerMessage notice(ClientMessage clientMessage) {
		logger.info(appport + "广播消息：" + clientMessage);
		logger.info("当前在线人数:" + simpUserRegistry.getUserCount());
		return new ServerMessage(clientMessage.getFromUserId() + " said: " + clientMessage.getMessage());
	}
	
	@MessageMapping("/send-msg")
//	@SendToUser("/queue/msg")
	public void sendToUser(ClientMessage clientMessage, Principal principal) {
		logger.info("P2P消息：" + clientMessage);
		// 消息发送到指定用户
		ServerMessage serverMessage = new ServerMessage(clientMessage.getFromUserId() + " said to you: " + clientMessage.getMessage());
//		simpMessagingTemplate.convertAndSendToUser(clientMessage.getToUserId().trim(), "/queue/msg", serverMessage);
		simpMessagingTemplate.convertAndSend("/amq/queue/msg", serverMessage);
//		return new ServerMessage("you said " + clientMessage.getMessage());
	}
	
	// 处理 @MessageMapping方法所抛出的异常
	@MessageExceptionHandler
	// 把错误信息发送到发布该消息的认证用户
	@SendToUser(destinations="/queue/error", broadcast=false)
	public ServerMessage handleExceptions(Exception e, ClientMessage clientMessage) {
		logger.error("Error handling message: {}, exp: {}", clientMessage, e.getMessage());
		return new ServerMessage("send msg error");
	}
}
