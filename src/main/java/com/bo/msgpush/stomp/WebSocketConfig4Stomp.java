package com.bo.msgpush.stomp;

import org.springframework.beans.factory.annotation.Value;
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
public class WebSocketConfig4Stomp implements WebSocketMessageBrokerConfigurer {

	@Value("${mq.rabbit.host}")
	private String host;
	
	@Value("${mq.rabbit.stomp.port}")
	private int port;
	
	@Value("${mq.rabbit.username}")
	private String username;
	
	@Value("${mq.rabbit.password}")
	private String password;
	
	/**
	 * 注册stomp端点用于客户端连接websocket
     * addEndpoint：添加一个服务端点映射地址，来接收客户端的连接; setAllowedOrigins：允许跨域
     * addInterceptors：添加拦截器; withSockJS：开启SockJS支持
     * 
	 * @param registry
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp") // 设置websocket端点
				.setAllowedOrigins("*") // 允许跨域请求
				.setHandshakeHandler(clientHandshakeHandler())
				.addInterceptors(clientHandshakeInterceptor())
				.withSockJS();// .setHeartbeatTime(25000)设置向SockJS客户端发送心跳时间间隔(默认25s)
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
		// 使用spring提供的简单内存消息代理
		// registry.enableSimpleBroker("/queue", "/topic");
		// 使用rabbitmq作为外部消息代理，以实现集群消息推送
		// rabbitmq合法的目的前缀：/temp-queue, /exchange, /topic, /queue, /amq/queue, /reply-queue/
		registry.enableStompBrokerRelay("/topic", "/queue", "/amq/queue", "/exchange") // 配置消息代理前缀
		.setRelayHost(host) // 配置消息代理服务器.默认:127.0.0.1
		.setRelayPort(port) // 配置代理服务器端口.默认:61613
		.setClientLogin(username).setClientPasscode(password) // 配置每个客户端的连接认证信息.默认:guest/guest
		.setSystemLogin(username).setSystemPasscode(password) // 源自服务端的连接的认证信息.默认:guest/guest
		.setUserRegistryBroadcast("/topic/simp-user-registry")// 当有用户注册时将其广播到其他服务器
		.setUserDestinationBroadcast("/topic/unresolved-user-destination")// 将当前服务端点无法发送到user dest的消息广播到其他服务端点处理
		.setSystemHeartbeatReceiveInterval(15000) // 配置服务端session接收stomp消息代理心跳时间间隔(0代表不接收)
		.setSystemHeartbeatSendInterval(15000); // 配置服务端session向stomp消息代理发送心跳时间间隔(0代表不接收)
		// 配置服务端接收消息的地址前缀与@MessageMapping路径组合使用
		registry.setApplicationDestinationPrefixes("/app");
		// 配置点对点使用的订阅前缀，默认是"/user" 例如：(@link org.springframework.messaging.simp.user.DefaultUserDestinationResolver)
		// 客户端订阅：/user/queue/message
		// 服务器推送指定用户：/user/{userId}/queue/message
		registry.setUserDestinationPrefix("/user");
	}

	/**
	 * 输入通道参数设置
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.taskExecutor()
			// 核心线程数默认为系统核数*2. 
			// 如果业务没大量io操作, client与server网络情况情况良好, 则默认配置就可以.
			// 如果业务方法有大量io操作, 那应当适当加大request channel的线程数, 以充分利用cpu.
			.corePoolSize(8) // 设置消息输入通道的线程池线程数
			.maxPoolSize(64)// 最大线程数
			// queueCapacity的默认配置是无限大;
			// 如果是无限大, 那么线程数则永远是核心线程数.
			// 只能当队列容积不够用时, 实际线程数才会大于核心线程数.
			.queueCapacity(1000) // 队列容积(默认Integer.MAX_VALUE)
			.keepAliveSeconds(60);// 线程活动时间
		registration.interceptors(clientInboundChannelInterceptor());
	}

	/**
	 * 输出通道参数设置
	 */
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		// 如果client与server之间网络连接不可控, 比如通过外网连接手机上的客户端, 则应该当适当加大response channel的线程数
		registration.taskExecutor()
			.corePoolSize(16)
			.maxPoolSize(32)
			.queueCapacity(2000)
			.keepAliveSeconds(60);
		registration.interceptors(clientOutboundChannelInterceptor());
	}

	/**
	 * 消息传输参数配置
	 */
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(64 * 1024) // 设置接收客户端消息字节数大小
				.setSendBufferSizeLimit(512 * 1024)// 设置消息缓存大小
				.setSendTimeLimit(15000); // 设置服务端消息发送时间限制毫秒
	}
	
	/**
	 * websocket握手处理器
	 * 
	 * @return
	 */
	@Bean
	public ClientHandshakeHandler clientHandshakeHandler() {
		return new ClientHandshakeHandler();
	}
	
	/**
	 * @return websocket握手拦截器
	 */
	@Bean
	public ClientHandshakeInterceptor clientHandshakeInterceptor() {
		return new ClientHandshakeInterceptor();
	}

	/**
	 * @return stomp入站通道拦截器
	 */
	@Bean
	public ChannelInterceptor clientInboundChannelInterceptor() {
		return new ClientInboundChannelInterceptor();
	}
	
	/**
	 * @return stomp出站通道拦截器
	 */
	@Bean
	public ChannelInterceptor clientOutboundChannelInterceptor() {
		return new ClientOutboundChannelInterceptor();
	}
	
}
