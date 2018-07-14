/**
 * 
 */
package com.bo.msgpush.service;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.bo.common.utils.JsonUtils;
import com.bo.msgpush.domain.ClientMessage;
import com.bo.msgpush.domain.ServerMessage;
import com.bo.msgpush.websocket.WebSocketInterceptor;

/**
 * @notes websocket handler
 * 
 * @author wangboc
 * 
 * @version 2018年7月6日 下午2:18:29
 */
@Service("webSocketHandlerService")
public class WebSocketHandlerService implements WebSocketHandler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, WebSocketSession> sessionHolder = new ConcurrentHashMap<>();
	
	public static final String USER_ENDPOINT_KEY = "user_endponit";
	
	@Value("${spring.application.name}")
	private String appname;
	
	@Value("${server.port}")
	private String serverport;
	
	@Resource
	private RedisClientService redisClientService;
	
	/* 建立连接后触发的回调
	 * @see org.springframework.web.socket.WebSocketHandler#afterConnectionEstablished(org.springframework.web.socket.WebSocketSession)
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String clientId = getClientId(session);
		if(StringUtils.isNotBlank(clientId)) {
			// 保存用户ws会话
			sessionHolder.put(clientId, session);
			// 保存用户服务端点
			redisClientService.cacheHash(USER_ENDPOINT_KEY, clientId, getEndpointValue(), 0);
			logger.info("clientId: " + clientId + " connected success...");
			logger.info("current online users: " + sessionHolder.size());
		}
	}

	/* 收到消息时触发的回调
	 * @see org.springframework.web.socket.WebSocketHandler#handleMessage(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.WebSocketMessage)
	 */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		try {
			ClientMessage clientMessage = JsonUtils.fromJson(message.getPayload().toString(), ClientMessage.class);
			if(clientMessage.getToUserId().equals("all")) {
				sendToAll(new TextMessage(clientMessage.getMessage()));
			}else {
				sendToUser(clientMessage.getToUserId(), new TextMessage(clientMessage.getMessage()));
			}
		} catch (Exception e) {
			logger.error("handle message error", e);
		}
	}

	/* 断开连接后触发的回调
	 * @see org.springframework.web.socket.WebSocketHandler#afterConnectionClosed(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.CloseStatus)
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String clientId = getClientId(session);
		sessionHolder.remove(clientId);
		session.close();
		// 从用户服务端点中移除
		redisClientService.deleteHash(USER_ENDPOINT_KEY, clientId);
		logger.info("clientId: " + clientId + " closed...");
	}
	
	/* 传输消息出错时触发的回调
	 * @see org.springframework.web.socket.WebSocketHandler#handleTransportError(org.springframework.web.socket.WebSocketSession, java.lang.Throwable)
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable t) throws Exception {
		logger.error("message transmission error --> clientId: " + getClientId(session), t.getCause());
		// 一定要移除
        sessionHolder.remove(getClientId(session));
	}

	/* 是否处理分片消息: 表示是否让WebSocket支持大文件的拆分处理，默认为false
	 * @see org.springframework.web.socket.WebSocketHandler#supportsPartialMessages()
	 */
	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	public boolean pushMessage(ServerMessage serverMessage) {
		if(serverMessage != null) {
			if(serverMessage.getDestId().equals("all")) {
				return sendToAll(new TextMessage(serverMessage.getMessage()));
			}else {
				return sendToUser(serverMessage.getDestId(), new TextMessage(serverMessage.getMessage()));
			}
		}
		return false;
	}
	
	/**
	 * 发送消息到指定用户
	 * @param clientId 指定用户的ID
	 * @param message websocket消息
	 */
	public boolean sendToUser(String clientId, TextMessage message) {
		WebSocketSession session = sessionHolder.get(clientId);
		if(session != null && session.isOpen()) {
			try {
				session.sendMessage(message);
				return true;
			} catch (IOException e) {
				logger.error("send to user error --> clientId: " + clientId, e);
			}
		}
		return false;
	}
	
	/**
	 * 发送消息到所有用户
	 * @param message websocket消息
	 */
	public boolean sendToAll(TextMessage message) {
		for(Entry<String, WebSocketSession> user : sessionHolder.entrySet()) {
			WebSocketSession session = user.getValue();
			if(session.isOpen()) {
				try {
					session.sendMessage(message);
				} catch (IOException e) {
					logger.error("send to user error --> clientId: " + user.getKey(), e);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 获取用户标识
	 * @param session
	 * @return
	 */
	private String getClientId(WebSocketSession session) {
		if(session != null) {
			return (String) session.getAttributes().get(WebSocketInterceptor.WEBSOCKET_USER_KEY);
		}
		return null;
	}
	
	private String getEndpointValue() {
		return appname + ":" + serverport;
	}
}
