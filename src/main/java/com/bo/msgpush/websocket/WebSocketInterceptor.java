/**
 * 
 */
package com.bo.msgpush.websocket;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * @notes 握手拦截器
 * 
 * @author wangboc
 * 
 * @version 2018年7月6日 下午2:41:47
 */
public class WebSocketInterceptor implements HandshakeInterceptor {

	public static final String WEBSOCKET_USER_KEY = "WEBSOCKET_USERID";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#beforeHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.util.Map)
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		logger.info("step info handshake interceptor...");
		if (request instanceof ServletServerHttpRequest) {
//			ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
//			HttpSession session = serverHttpRequest.getServletRequest().getSession(false);
			String requestUri = request.getURI().toString();
			String clientId = requestUri.substring(requestUri.lastIndexOf("/") + 1);
			if(StringUtils.isNotBlank(clientId)) {
				logger.info("linked client ID: " + clientId);
				attributes.put(WEBSOCKET_USER_KEY, clientId);
			}
		}
		return true;// 返回false则拒绝连接
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#afterHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.lang.Exception)
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
		logger.info("step out handshake interceptor...");
	}

}
