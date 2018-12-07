package com.bo.msgpush.stomp.interceptor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import com.bo.msgpush.constant.MsgPushConst;
import com.bo.msgpush.service.RedisClientService;

/**
 * @Description 控制拦截用于传输发送给webSocket客户端的消息
 * @author Bo
 * @version 2018年7月15日 下午10:32:34
 * @码云 https://gitee.com/LeisureBo
 */
public class ClientOutboundChannelInterceptor implements ChannelInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RedisClientService redisClientService;
    
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			switch (accessor.getCommand()) {
				case CONNECTED:
					// 连接成功前置处理..
					if (accessor.getUser() != null) {
//						logger.info(accessor.getUser() + " connected pre-processing...");
					}
					break;
				case RECEIPT:
					// receipt应答前置处理..
//					logger.info("receipt pre-processing ..");
					
					break;
				case MESSAGE:
					
					break;
				case ERROR:
					// 错误消息处理
//					logger.error("error msg pre-processing ..");
					
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
					// 连接成功后置处理..
					// At this point the STOMP session can be considered fully established.
					// 将连接用户保存到redis(缓存24小时)
					if (accessor.getUser() != null) {
						logger.info(accessor.getUser() + " connected post-processing...");
						/*redisClientService.cacheValue(accessor.getUser().getName(), 
								MsgPushConst.OnlineStatus.ONLINE.value().toString(), 24 * 60 * 60 * 1000);*/
					}
					break;
				case RECEIPT:
					// receipt应答后置处理..
//					logger.info("receipt post-processing ..");
					
					break;
				case MESSAGE:
					
					break;
				case ERROR:
					// 错误消息处理
//					logger.error("error msg post-processing ..");
					
					break;
				default:
					// dosomething ..
					break;
			}
		}
	}
	
}
