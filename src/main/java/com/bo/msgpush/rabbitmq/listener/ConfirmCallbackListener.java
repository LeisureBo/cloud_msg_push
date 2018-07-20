/**
 * 
 */
package com.bo.msgpush.rabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

/**
 * @notes 消息确认监听器
 * 
 * @author wangboc
 * 
 * @version 2018年7月10日 下午2:08:46
 */
@Service
public class ConfirmCallbackListener implements RabbitTemplate.ConfirmCallback {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 1.echange存在时,routeKey路由无论是否存在,ack为true 
	 * 2.echange不存在时,ack为false。
	 * 3.当exchange为null或者空字符串时,ack为true 
	 * 4.网络中断会导致无confirm
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if(!ack) {
			logger.info("send message ack failed: " + cause + ", ID: " + String.valueOf(correlationData));
		}else {
			logger.info("send message ack success"); // 只确认生产者消息发送成功，消费者是否处理成功不做保证
		}
	}

}
