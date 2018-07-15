package com.bo.msgpush.stomp;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import com.bo.msgpush.domain.ClientAuthInfo;

/**
 * @Description 控制拦截用于传输从webSocket客户端接收的消息
 * @author wangboc
 * @version 2018年6月27日　上午12:25:34
 * @码云 https://gitee.com/LeisureBo
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ClientInboundChannelInterceptor implements ChannelInterceptor {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
    
	/**
	 * 客户端入站消息前置处理
	 * 
	 * @return 返回null将不会处理客户端的相应指令消息
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			// 判断客户端的连接状态
	        switch(accessor.getCommand()) {
		        case CONNECT:
		        	// 用户请求连接前置处理: 用户认证等..
		        	// to-do: 如何实现单用户多会话连接?
		        	List<String> tokens = accessor.getNativeHeader("token");
		        	if(tokens != null && !tokens.isEmpty()) {
		        		Principal principal = verifyToken(tokens.get(0));
		        		if (principal != null) {
		        			// 设置当前访问器的认证用户: User类需要实现Principal接口
							accessor.setUser(principal);
							return message;
						}
		        	}
		        	// 手动抛出异常以便触发客户端errorCallback
					throw new RuntimeException("User authentication failure");
		        case DISCONNECT:
		        	// 客户端请求断开连接前置处理..
		        	
		            break;
		        case SUBSCRIBE:
		        	// 用户订阅请求前置处理..防止用户订阅不合法频道..
		        	
		        	break;
		        case UNSUBSCRIBE:
		        	// 用户取消订阅请求前置处理..防止用户订阅不合法频道..
		        	
		        	break;
		        case SEND:
		        	// 用户发送信息前置处理..
		        	if(accessor.getUser() != null) {
		        		logger.info(accessor.getUser() + "send msg pre-processing...");
		        	}
		        	break;
		        default:
		        	// dosomething ..
		            break;
			}
		}
		return message;
	}

	/**
	 * 客户端入站消息后置处理
	 */
	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			switch (accessor.getCommand()) {
				case CONNECT:
					// 用户请求连接后置处理..
					
					break;
				case DISCONNECT:
					// 用户请求断开连接后置处理..
					// In some cases this event may be published more than once per session. 
					// Components should be idempotent with regard to multiple disconnect events.
					if (accessor.getUser() != null) {
						logger.info(accessor.getUser() + " disconnected ...");
					}
					break;
				case SUBSCRIBE:
					// 用户请求订阅后置处理..
					if(accessor.getUser() != null) {
						logger.info(accessor.getUser() + " subscribe " + accessor.getDestination() + " succeed ...");
//						simpMessagingTemplate.convertAndSendToUser("/topic/msg","订阅消息发送成功");
					}
					break;
		        case UNSUBSCRIBE:
		        	// 用户取消订阅请求后置处理..防止用户订阅不合法频道..
		        	
		        	break;
		        case SEND:
		        	// 用户发送信息后置处理..
		        	if(accessor.getUser() != null) {
		        		logger.info(accessor.getUser() + "send msg post-processing...");
		        	}
		        	break;
				default:
					// dosomething ..
					break;
			}
		}
	}
	
    /**
     * 解析token并获取认证用户信息
     * 
     * @param token
     * @return
     */
    private Principal verifyToken(String token) {
    	if(true) {
    		return new ClientAuthInfo(token);
    	}
    	return null;
    }
}
