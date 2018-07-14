package com.bo.msgpush.stomp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @Description springboot整合websocket配置: 基于stomp协议的配置
 * @Author 王博
 * @Version 2018年6月26日　上午9:41:35
 * @码云 https://gitee.com/LeisureBo
 */
@Configuration
// 注解开启使用STOMP协议来传输基于代理(message broker)的消息,这时控制器支持使用@MessageMapping,就像使用@RequestMapping一样
@EnableWebSocketMessageBroker
public class WebSocketConfigForStomp implements WebSocketMessageBrokerConfigurer {

	/**
	 * 注册stomp端点用于客户端连接websocket
     * addEndpoint：添加一个服务端点映射地址，来接收客户端的连接; setAllowedOrigins：允许跨域
     * addInterceptors：添加拦截器; withSockJS：开启SockJS支持
     * 
	 * @param registry
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp")
				.setAllowedOrigins("*")
				//.setHandshakeHandler(stompMsgHandshakeHandler())
				.addInterceptors(httpSessionHandshakeInterceptor())
				.withSockJS().setHeartbeatTime(25000);// 设置心跳时间间隔(默认25s)
	}
	
	/**
	 * 配置消息代理: 启用简单的消息代理，并配置一个或多个前缀来过滤针对代理的目的地
	 * 
	 * 其中"/app"开始的消息被路由到消息处理方法（即应用程序工作），其目的地从"/topic"或"/user"开始的消息将被路由到消息代理。
	 * "/app"前缀是任意的，你可以选择任何前缀。 它只是为了将消息路由到消息处理方法，以便将应用程序工作与消息路由到代理以向订阅客户端广播。
	 * "/topic"和"/queue"前缀取决于正在使用的代理。 在简单的内存代理中，前缀没有任何特殊含义，它仅仅是一个约定，指示如何使用目的地。（pub-sub针对的是多个订阅者或通常针对单个收件人的点对点消息）。 
	 * 在使用专用代理的情况下，大多数代理使用"topic"作为带pub-sub语义的目的地的前缀，使用"/queue"作为具有点对点语义的目的地。 
	 * 
	 * @param registry
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 使用rabbitmq作为消息代理
		registry.enableStompBrokerRelay("/topic", "/amq/queue") // 配置消息代理前缀
		.setRelayHost("10.0.8.170") // 配置消息代理服务器
		.setClientLogin("guest").setClientPasscode("guest") // 配置每个客户端的连接认证信息
		.setSystemLogin("guest").setSystemPasscode("guest") // 源自服务端的连接的认证信息
		.setUserRegistryBroadcast("/topic/simp-user-registry")// 当有用户注册时将其广播到其他服务器
		.setSystemHeartbeatReceiveInterval(2000000000) // 配置服务端接收客户端心跳的间隔
		.setSystemHeartbeatSendInterval(2000000000); // 配置向客户端发送心跳消息的间隔
		// 配置服务端接收消息的地址前缀与@MessageMapping路径组合使用
		registry.setApplicationDestinationPrefixes("/app");
		// 配置点对点使用的订阅前缀，默认是"/user"
		// 客户端订阅：/user/queue/message
		// 服务器推送指定用户：/user/{userId}/queue/message
		registry.setUserDestinationPrefix("/user");
	}

	/**
	 * 输入通道参数设置
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.taskExecutor().corePoolSize(4) // 设置消息输入通道的线程池线程数
				.maxPoolSize(8)// 最大线程数
				.keepAliveSeconds(60);// 线程活动时间
		registration.interceptors(channelInterceptor());
	}

	/**
	 * 输出通道参数设置
	 */
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.taskExecutor().corePoolSize(4).maxPoolSize(8).keepAliveSeconds(60);
		registration.interceptors(channelInterceptor());
	}

	/**
	 * 消息传输参数配置
	 */
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(8192) // 设置消息字节数大小
				.setSendBufferSizeLimit(8192)// 设置消息缓存大小
				.setSendTimeLimit(10000); // 设置消息发送时间限制毫秒
	}
	
	/**
	 * Stomp握手处理器
	 * 
	 * @return
	 */
	@Bean
	public StompMsgHandshakeHandler stompMsgHandshakeHandler() {
		return new StompMsgHandshakeHandler();
	}
	
	/**
	 * @return websocket握手handshake拦截器
	 */
	@Bean
	public HttpSessionHandshakeInterceptor httpSessionHandshakeInterceptor() {
		return new CustomHandshakeInterceptor();
	}

	/**
	 * @return stomp连接处理拦截器
	 */
	@Bean
	public ChannelInterceptor channelInterceptor() {
		return new CustomChannelInterceptor();
	}
	
}
