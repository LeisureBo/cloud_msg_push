/**
 * 
 */
package com.bo.msgpush.rabbitmq.listener;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.bo.common.utils.JsonUtils;
import com.bo.msgpush.domain.msg.ClientMessage;
import com.bo.msgpush.service.WebSocketHandlerService;

/**
 * @notes 系统普通消息监听
 * 
 * @author wangboc
 * 
 * @version 2018年7月9日 下午1:53:10
 */
@Service
public class MessageListener4Common implements ChannelAwareMessageListener {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private WebSocketHandlerService webSocketHandlerService;
	
	/* 
	 * 1、处理成功，这种时候用basicAck确认消息；
     * 2、可重试的处理失败，这时候用basicNack将消息重新入列；
     * 3、不可重试的处理失败，这时候使用basicNack将消息丢弃。
	 * @see org.springframework.amqp.rabbit.core.ChannelAwareMessageListener#onMessage(org.springframework.amqp.core.Message, com.rabbitmq.client.Channel)
	 */
	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		String msgjson = new String(message.getBody(), "UTF-8");
		logger.info("CommonMessageListener onMessage --> " + msgjson);
		try {
			ClientMessage clientMessage = JsonUtils.fromJson(msgjson, ClientMessage.class);
			clientMessage.setType("common");
			boolean success = webSocketHandlerService.pushMessage(clientMessage);
			/**
			 * basicNack(long deliveryTag, boolean multiple, boolean requeue)
			 * deliveryTag:该消息的index 
			 * multiple：是否批量. true:将一次性拒绝所有小于deliveryTag的消息。
			 * requeue：被拒绝的是否重新入队列
			 */
			if(success) {
				logger.info("consume common message success");
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);// false只确认当前一个消息收到，true确认所有consumer获得的消息
			// 如果消息已被处理失败2次则拒绝接收
			}else if(message.getMessageProperties().getRedelivered()){
				Thread.sleep(1000);// 避免过多失败log
				logger.info("common message has been redelivered and will be rejected");
				channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);// 拒绝消息且不返回队列
			}else {
				Thread.sleep(1000);// 避免过多失败log
				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);// 重新发回队列
			}
		} catch (Exception e) {
			logger.error("handle common message error", e);
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);// 丢弃消息
		}
	}
	
}
