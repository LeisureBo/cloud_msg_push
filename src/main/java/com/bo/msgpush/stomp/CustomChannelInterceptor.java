package com.bo.msgpush.stomp;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import com.bo.msgpush.domain.ClientAuthInfo;

/**
 * @Description Channel客户端渠道连接拦截器：监听用户连接情况
 * To-Do: 为什么preSend和postSend拦截方法在实际运行时会执行2次？
 * @author wangboc
 * @version 2018年6月27日　上午12:25:34
 * @码云 https://gitee.com/LeisureBo
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class CustomChannelInterceptor implements ChannelInterceptor {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			// 判断客户端的连接状态
	        switch(accessor.getCommand()) {
		        case CONNECT:
		        	List<String> tokens = accessor.getNativeHeader("token");
		        	if(tokens != null && !tokens.isEmpty()) {
		        		Principal principal = verifyToken(tokens.get(0));
		        		if (principal != null) {
		        			// 设置当前访问器的认证用户: User类需要实现Principal接口
							accessor.setUser(principal);
							logger.info(accessor.getUser() + " connected ...");
							return message;
						}
		        	}
		        	// 手动抛出异常以便触发客户端errorCallback
					throw new RuntimeException("User authentication failure");
		        case CONNECTED:
		        	
		            break;
		        case DISCONNECT:
		        	if(accessor.getUser() != null) {
		        		logger.info(accessor.getUser() + " disconnected ...");
		        	}
		            break;
		        case SUBSCRIBE:
		        	String str = accessor.getDestination();
		        	String prefix = "/custom/";
		        	String substr = str.substring(0, prefix.length());
		        	if(substr.equals(prefix)) {
		        		String actualstr = "/amq/" + str.substring(prefix.length());
		        		accessor.setDestination(actualstr) ;
		        	}
		        	break;
		        default:
		            break;
			}
		}
		return message;
	}

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		
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
