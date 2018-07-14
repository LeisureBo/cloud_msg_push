/**
 * 
 */
package com.bo.msgpush.rabbitmq.config;

import java.io.IOException;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.bo.msgpush.rabbitmq.listener.MessageListener4Common;
import com.bo.msgpush.rabbitmq.listener.MessageListener4Play;


/**
 * @notes rabbitmq config
 * 
 * @author wangboc
 * 
 * @version 2018年7月6日 下午4:33:40
 */
@Configuration
public class RabbitMQConfig {

	@Value("${mq.rabbit.address}")
	private String address;
	
	@Value("${mq.rabbit.username}")
	private String username;
	
	@Value("${mq.rabbit.password}")
	private String password;
	
	@Value("${mq.rabbit.virtualHost}")
	private String virtualHost;
	
	@Value("${mq.rabbit.sessionCacheSize}")
	private int sessionCacheSize;
	
	@Value("${mq.rabbit.maxConsumers}")
	private int maxConcurrentConsumers;
	
	@Value("${mq.concurrent.consumers}")
	private int concurrentConsumers;
	
	@Value("${mq.prefetch.count}")
	private int prefetchCount;
	
	@Value("${mq.rabbit.exchange.name}")
	private String exchangeName;

	@Value("${mq.rabbit.queue.common}")
	private String commonQueue;
	
	@Value("${mq.rabbit.queue.play}")
	private String playQueue;
	
	@Value("${mq.rabbit.routingkey.common}")
	private String commonRoutingKey;
	
	@Value("${mq.rabbit.routingkey.play}")
	private String playRoutingKey;
	
	
	@Bean
	public Queue commonQueue() {
		return new Queue(commonQueue, true);// 队列持久
	}
	
	@Bean
	public Queue msgQueue() {
		return new Queue("msg", true);// 队列持久
	}
	
	@Bean
	public Queue playQueue() {
		return new Queue(playQueue, true);// 队列持久
	}
	
	@Bean
	public TopicExchange defaultExchange() {
		return new TopicExchange(exchangeName);
	}
	
	@Bean
	public Binding bindingExchangeCommon(Queue commonQueue, TopicExchange defaultExchange) {
		return BindingBuilder.bind(commonQueue).to(defaultExchange).with(commonRoutingKey);
	}
	
	@Bean
	public Binding bindingExchangePlay(Queue playQueue, TopicExchange defaultExchange) {
		return BindingBuilder.bind(playQueue).to(defaultExchange).with(playRoutingKey);
	}
	
	@Bean // 创建连接工厂
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(address);
		connectionFactory.setVirtualHost(virtualHost);
		connectionFactory.setPublisherConfirms(true);// 允许设置消息回调
		connectionFactory.setChannelCacheSize(sessionCacheSize);// 设置连接数量
		// connectionFactory.setUsername(username);
		// connectionFactory.setPassword(password);
		return connectionFactory;
	}
	
	@Bean // 创建消息发送模板
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // 必须是prototype类型(便于不同的消息回调)
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		return template;
	}
	
	
	@Bean // 创建监听器
	public SimpleMessageListenerContainer playMessageContainer(MessageListener4Play playMessageListener) throws AmqpException, IOException {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueueNames(playQueue);// 设置监听的队列
		container.setExposeListenerChannel(true);
		container.setPrefetchCount(prefetchCount);// 设置每个消费者获取的最大的消息数量
        container.setMaxConcurrentConsumers(maxConcurrentConsumers);// 最大消费者数量
		container.setConcurrentConsumers(concurrentConsumers);// 消费者数量
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);// 设置确认模式为手工确认
		container.setMessageListener(playMessageListener);// 监听处理类
		return container;
	}
	
	@Bean // 创建监听器
	public SimpleMessageListenerContainer commonMessageContainer(MessageListener4Common commonMessageListener) throws AmqpException, IOException {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueueNames(commonQueue);// 设置监听的队列
		container.setExposeListenerChannel(true);
		container.setPrefetchCount(prefetchCount);// 设置每个消费者获取的最大的消息数量
		container.setMaxConcurrentConsumers(maxConcurrentConsumers);// 最大消费者数量
		container.setConcurrentConsumers(concurrentConsumers);// 消费者数量
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);// 设置确认模式为手工确认
		container.setMessageListener(commonMessageListener);// 监听处理类
		return container;
	}
	
}
