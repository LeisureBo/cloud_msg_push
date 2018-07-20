/**
 * 
 */
package com.bo.msgpush.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bo.common.domain.ProcessResult;
import com.bo.msgpush.domain.OperMessage;
import com.bo.msgpush.domain.UserMessage;


/**
 * @notes 对外消息推送接口
 * 
 * @author wangboc
 * 
 * @version 2018年7月17日 下午2:18:41
 */
@RestController
@RequestMapping("/msgpush")
public class MsgPushController {
	
	@Value("${push.exchange.topic}")
	private String topicExchange;// 广播消息交换机
	
	@Value("${push.exchange.user}")
	private String userExchange;// p2p消息交换机
	
	@Value("${push.suffix.sub.uer}")
	private String userSubSuffix;// p2p消息订阅后缀
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private RabbitTemplate rabbitTemplate;
	
	@PostMapping("/send_oms")
	public ProcessResult sendOperMsg(@RequestBody OperMessage operMessage) {
		ProcessResult processResult = new ProcessResult();
		try {
			// 消息属性设置
			MessagePostProcessor postProcessor = new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setPriority(4);
					message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
					// ...
					return message;
				}
			};
			// rabbitTemplate发送消息默认持久化
			rabbitTemplate.convertAndSend(topicExchange, operMessage.getRoutingKey(), operMessage);
		} catch (AmqpException e) {
			processResult.setRetCode(-1);
			logger.error("sendOperMsg error", e);
		}
		return processResult;
	}
	
	@PostMapping("/send_ums")
	public ProcessResult sendUserMsg(@RequestBody UserMessage userMessage) {
		ProcessResult processResult = new ProcessResult();
		try {
			rabbitTemplate.convertAndSend(userExchange, getUserRoutingKey(userMessage.getTargetUid()), userMessage);
		} catch (AmqpException e) {
			processResult.setRetCode(-1);
			logger.error("sendUserMsg error", e);
		}
		return processResult;
	}
	
	private String getUserRoutingKey(String userId) {
		return userSubSuffix + "." + userId;
	}
}
