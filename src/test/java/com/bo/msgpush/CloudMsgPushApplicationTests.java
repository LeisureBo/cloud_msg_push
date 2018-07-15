package com.bo.msgpush;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bo.common.utils.JsonUtils;
import com.bo.msgpush.domain.ClientMessage;
import com.bo.msgpush.rabbitmq.listener.ConfirmCallbackListener;
import com.bo.msgpush.rabbitmq.listener.ReturnCallBackListener;
import com.bo.msgpush.service.RedisClientService;
import com.bo.msgpush.service.WebSocketHandlerService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudMsgPushApplicationTests {

	private RabbitTemplate rabbitTemplate;
	
	@Resource
	private RedisClientService redisClientService;
	
	@Resource
	private ConfirmCallbackListener confirmCallback;
	
	@Resource
	private ReturnCallBackListener returnCallback;
	
	private String commonRoutingKey = ":common.msg";
	
	private String exchangeName = ":topic_exchange";
	
	@Autowired
	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
	}

//	@Test
	public void contextLoads() throws IOException {
		ClientMessage clientMessage = new ClientMessage();
		clientMessage.setMessage("hello kitty.");
		clientMessage.setFromUserId("9");
		clientMessage.setToUserId("21");
		String userEndpoint = redisClientService.getHashValue(WebSocketHandlerService.USER_ENDPOINT_KEY, clientMessage.getToUserId());
		userEndpoint = userEndpoint == null ? "" : userEndpoint;
		rabbitTemplate.convertAndSend(userEndpoint + exchangeName, userEndpoint + commonRoutingKey, JsonUtils.toJson(clientMessage));
		System.in.read();
	}
	
	@Test
	public void sendMsgToStompBroker() throws IOException {
		ClientMessage clientMessage = new ClientMessage();
		clientMessage.setMessage("System Info: Goodnight Everyone!");
		clientMessage.setFromUserId("System");
		clientMessage.setToUserId("21");
		rabbitTemplate.convertAndSend("amq.topic", "notice", JsonUtils.toJson(clientMessage));
		System.in.read();
	}
	

}
