package com.bo.msgpush.stomp;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * @Description 获取客户端连接前对客户端的session、cookie等信息进行握手处理， 也就是可以在这里可以进行一些用户认证？
 * @Author 王博
 * @Version 2018年6月28日　下午4:17:46
 * @码云 https://gitee.com/LeisureBo
 */
public class StompMsgHandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		return super.determineUser(request, wsHandler, attributes);
	}
	
}
