/**
 * 
 */
package com.bo.msgpush.websocket;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.bo.msgpush.service.WebSocketHandlerService;

/**
 * @notes websocket configuration
 * 
 * @author wangboc
 * 
 * @version 2018年7月6日 下午2:32:05
 */
//@Configuration
//@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

	@Resource
	private WebSocketHandlerService webSocketHandlerService;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.config.annotation.WebSocketConfigurer#registerWebSocketHandlers(org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry)
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandlerService, "/websocket/{clientId}").setAllowedOrigins("*").addInterceptors(new WebSocketInterceptor());
		// 允许客户端使用SockJs
//		registry.addHandler(webSocketHandlerService, "/ws/sockjs").setAllowedOrigins("*").addInterceptors(new WebSocketInterceptor()).withSockJS();
	}

}
