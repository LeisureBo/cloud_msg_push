/**
 * 
 */
package com.bo.msgpush.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import com.bo.msgpush.domain.AbsMessage;
import com.bo.msgpush.domain.MessageBody;
import com.bo.msgpush.domain.OperMessage;
import com.bo.msgpush.domain.UserMessage;

/**
 * @notes stomp客户端消息发布接口
 * 
 * @author wangboc
 * 
 * @version 2018年7月17日 下午2:31:53
 */
@Controller
public class StompClientController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${server.port}")
	private String appPort;// 应用端口号
	
	@Value("${push.exchange.topic}")
	private String topicExchange;// 广播消息交换机
	
	@Value("${push.exchange.operation}")
	private String operExchange;// 运营消息交换机
	
	@Value("${push.exchange.user}")
	private String userExchange;// p2p消息交换机
	
	@Value("${push.suffix.sub.user}")
	private String userSubSuffix;// p2p消息订阅后缀
	
	@Resource
	private SimpUserRegistry simpUserRegistry;
	
	@Resource
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@MessageMapping("/send_tms")
	public OperMessage sendTopicMsg(OperMessage operMessage, Principal principal) {
		logger.info("[port: " + appPort + "|online: " + simpUserRegistry.getUserCount() + "] Topic消息：" + operMessage);
		Map<String, Object> headers = new HashMap<>();
		if(operMessage.getMsgHeader() != null) {
			headers.put("persistent", operMessage.getMsgHeader().getPersistent());
			headers.put("priority", operMessage.getMsgHeader().getPriority());
		}
		String destination = "/exchange/" + topicExchange + "/" + operMessage.getRoutingKey();
		simpMessagingTemplate.convertAndSend(destination, operMessage, headers);
		return operMessage;
	}
	
	@MessageMapping("/send_oms")
	public OperMessage sendOperMsg(OperMessage operMessage, Principal principal) {
		logger.info("[port: " + appPort + "|online: " + simpUserRegistry.getUserCount() + "] 运营消息：" + operMessage);
		Map<String, Object> headers = new HashMap<>();
		if(operMessage.getMsgHeader() != null) {
			headers.put("persistent", operMessage.getMsgHeader().getPersistent());
			headers.put("priority", operMessage.getMsgHeader().getPriority());
		}
		String destination = "/exchange/" + operExchange + "/" + operMessage.getRoutingKey();
		simpMessagingTemplate.convertAndSend(destination, operMessage, headers);
		return operMessage;
	}

	@MessageMapping("/send_ums")
	public UserMessage sendUserMsg(UserMessage userMessage, Principal principal) {
		logger.info("[port: " + appPort + "|online: " + simpUserRegistry.getUserCount() + "] P2P消息：" + userMessage);
		Map<String, Object> headers = new HashMap<>();
		if(userMessage.getMsgHeader() != null) {
			headers.put("persistent", userMessage.getMsgHeader().getPersistent());
			headers.put("priority", userMessage.getMsgHeader().getPriority());
		}
		String destination = "/exchange/" + userExchange + "/" + getUserRoutingKey(userMessage.getTargetUid());
		simpMessagingTemplate.convertAndSend(destination, userMessage, headers);
		return userMessage;
	}

	@MessageExceptionHandler // 处理消息发送异常
	public void handleExceptions(Exception exp, AbsMessage message, Principal principal) {
		logger.error("Error handling message: {}, exp: {}", message , exp.getMessage());
		String destination = "/exchange/" + userExchange + "/" + getUserRoutingKey(principal.getName());
		UserMessage errorMessage = new UserMessage();
		MessageBody msgBody = new MessageBody();
		msgBody.setTitle("ERROR");
		msgBody.setContent("sorry, send msg error..");
		errorMessage.setMsgBody(msgBody);
		simpMessagingTemplate.convertAndSend(destination, errorMessage);
	}
	
	private String getUserRoutingKey(String userId) {
		return userSubSuffix + "." + userId;
	}
}
