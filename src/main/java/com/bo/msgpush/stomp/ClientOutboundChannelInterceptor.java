package com.bo.msgpush.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

/**
 * @Description 控制拦截用于传输发送给webSocket客户端的消息
 * @author Bo
 * @version 2018年7月15日 下午10:32:34
 * @码云 https://gitee.com/LeisureBo
 */
public class ClientOutboundChannelInterceptor implements ChannelInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			switch (accessor.getCommand()) {
				case CONNECTED:
					// 连接成功前处理..
					if (accessor.getUser() != null) {
						logger.info(accessor.getUser() + " connected pre-processing...");
					}
					break;
				case ERROR:
					// 错误消息处理
					logger.error("error msg pre-processing ..");
					
					break;
				default:
					// dosomething ..
					break;
			}
		}
		return message;
	}

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			switch (accessor.getCommand()) {
				case CONNECTED:
					// 连接成功后处理..
					// At this point the STOMP session can be considered fully established.
					if (accessor.getUser() != null) {
						logger.info(accessor.getUser() + " connected post-processing...");
					}
					break;
				case ERROR:
					// 错误消息处理
					logger.error("error msg post-processing ..");
					
					break;
				default:
					// dosomething ..
					break;
			}
		}
	}
	
}
