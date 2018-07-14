/**
 * 
 */
package com.bo.msgpush.rabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @notes 消息发送失败返回监听器：如路由不到队列时触发回调
 * 
 * @author wangboc
 * 
 * @version 2018年7月10日 下午2:11:19
 */
@Service
public class ReturnCallBackListener implements RabbitTemplate.ReturnCallback{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		logger.info("send message failed...");
	}

}
