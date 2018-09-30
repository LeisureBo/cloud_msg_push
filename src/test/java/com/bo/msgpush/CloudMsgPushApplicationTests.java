package com.bo.msgpush;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.bo.common.utils.JsonUtils;
import com.bo.msgpush.domain.msg.ClientMessage;
import com.bo.msgpush.rabbitmq.listener.ConfirmCallbackListener;
import com.bo.msgpush.rabbitmq.listener.ReturnCallBackListener;
import com.bo.msgpush.service.RedisClientService;
import com.bo.msgpush.service.WebSocketHandlerService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudMsgPushApplicationTests {

	// 通过RabbitTemplate发送的消息默认持久化
	private RabbitTemplate rabbitTemplate;
	
	// 通过spring stomp websocket发送到mq的消息默认非持久化
	@Resource
	private SimpMessagingTemplate simpMessagingTemplate;
	
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
		// 回调配置移到#RabbitMQConfig配置类
//		rabbitTemplate.setConfirmCallback(confirmCallback);
//		rabbitTemplate.setReturnCallback(returnCallback);
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
		clientMessage.setMessage("System Info: Good evening everyone!");
		clientMessage.setFromUserId("System");
		clientMessage.setToUserId("21");
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("persistent", true);
		for(int i=1; i<6;i++) {
			clientMessage.setMessage("system info: " + i);
//			rabbitTemplate.convertAndSend("amq.topic", "msg.Lily", JsonUtils.toJson(clientMessage));
			rabbitTemplate.convertAndSend("stomp-subscription-wD_WPz0YtxNUlNdM25efTg", JsonUtils.toJson(clientMessage));// 发送到默认交换机，路由键为队列名称
//			simpMessagingTemplate.convertAndSend("/queue/test", clientMessage, headers);
//			simpMessagingTemplate.convertAndSend("/exchange/amq.direct/test", clientMessage, headers);
		}
//		System.in.read();
	}

}
