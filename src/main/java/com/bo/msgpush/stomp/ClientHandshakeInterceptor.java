package com.bo.msgpush.stomp;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @Description websocket握手拦截器  一般用于握手之前将用户信息交给WebSocketSession管理之后可以在WebSocket处理类中获取用户信息
 * @author wangboc
 * @version 2018年6月26日　下午11:56:05
 */
public class ClientHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
        // 解决The extension [x-webkit-deflate-frame] is not supported问题
        if(request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        logger.info("Http协议转换Websoket协议进行握手前 -->" + request.getURI());
        HttpSession session = getSession(request);
        // to-do 为什么此处获取不到session?
        if(session != null) {
        	// 把sessionId和accountId保存到WebsocketSession
//        	attributes.put(WebSocketConst.SESSION_KEY_ACCT_ID, session.getAttribute(WebSocketConst.SESSION_KEY_ACCT_ID));
//        	attributes.put(WebSocketConst.SESSION_ID, session.getId());
        	logger.info("SessionId: " + session.getId());
//        	logger.info("AccountId: " + session.getAttribute(WebSocketConst.SESSION_KEY_ACCT_ID));
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
	}

	@Nullable
	private HttpSession getSession(ServerHttpRequest request) {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
			return serverRequest.getServletRequest().getSession(isCreateSession());
		}
		return null;
	}
	
	/**
	 * 在握手之后执行该方法. 无论是否握手成功都指明了响应状态码和响应头.
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {
		logger.info("Http协议转换Websoket协议握手成功后 --> " + response.getHeaders().getDate());
	}
	
}
