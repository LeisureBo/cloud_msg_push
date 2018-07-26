package com.bo.msgpush.stomp;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import com.bo.common.utils.JsonUtils;
import com.bo.msgpush.domain.ClientAuthInfo;
import com.rabbitmq.client.Channel;

/**
 * @Description 控制拦截用于传输从webSocket客户端接收的消息
 * @author wangboc
 * @version 2018年6月27日　上午12:25:34
 * @码云 https://gitee.com/LeisureBo
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ClientInboundChannelInterceptor implements ChannelInterceptor {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${push.exchange.topic}")
	private String topicExchange;// 广播消息交换机 "amq.topic"
	
	@Value("${push.exchange.operation}")
	private String operExchange;// 运营消息交换机 "amq.topic"
	
	@Value("${push.exchange.user}")
	private String userExchange;// p2p消息交换机 "amq.direct"
    
	@Value("${push.prefix.sub.topic}")
    private String topicSubPrefix;// 广播消息订阅前缀 "/topic/"
	
	@Value("${push.prefix.sub.operation}")
	private String operSubPrefix;// 运营消息订阅前缀 "/oper/"
    
    @Value("${push.prefix.sub.user}")
    private String userSubPrefix;// p2p消息订阅前缀 "/user/"
    
    @Value("${push.prefix.exclude}")
    private String excludePrefixs;// 需要排除校验的前缀
    
    @Value("${push.prefix.broker}")
    private String brokerPrefix;// 消息代理前缀 "/amq/queue/"
    
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
    
	@Resource
	private ConnectionFactory connectionFactory;
	
	private Set<String> excludePrefixSet;
	
	@PostConstruct
	public void init() {
		String[] excludePrefixArr = StringUtils.split(excludePrefixs, ",");
		excludePrefixSet = new HashSet<>(Arrays.asList(excludePrefixArr));
	}
	
	/**
	 * 客户端入站消息前置处理
	 * 
	 * @return 返回null将不会处理客户端的相应指令消息
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			// 判断客户端的连接状态
	        switch(accessor.getCommand()) {
		        case CONNECT:
		        	// 用户请求连接前置处理: 用户认证等..
		        	// stomp如何实现单用户多会话连接?
		        	// 当同一个用户ID在不同终端登录时会生成多个sessionId这时这个userId就会绑定多个sessionId;
		        	// 每个sessionId绑定着自己的订阅主题,其他用户向其发送消息时会传播到该userId下的所有session
		        	List<String> tokens = accessor.getNativeHeader("token");
		        	if(tokens != null && !tokens.isEmpty()) {
		        		Principal principal = verifyToken(tokens.get(0));
		        		if (principal != null) {
		        			// 设置当前访问器的认证用户: User类需要实现Principal接口
							accessor.setUser(principal);
							return message;
						}
		        	}
		        	// 手动抛出异常以便触发客户端errorCallback
					throw new RuntimeException("User authentication failure");
		        case DISCONNECT:
		        	// 客户端请求断开连接前置处理..
		        	
		            break;
		        case SUBSCRIBE:
		        	// 用户订阅请求前置处理..防止用户订阅不合法频道..
		        	this.configSubDest(accessor);
		        	break;
		        case UNSUBSCRIBE:
		        	// 用户取消订阅请求前置处理..防止用户订阅不合法频道..
		        	
		        	break;
		        case SEND:
		        	// 用户发送信息前置处理..
		        	if(accessor.getUser() != null) {
		        		logger.info(accessor.getUser() + "send msg pre-processing...");
		        	}
		        	break;
		        case ACK:
		        	// 客户端确认ack前置处理..
		        	//String ackHeader = accessor.getAck();
		        	//nativeHeaders={receipt=[my receipt], message-id=[T_sub-1@@session-QxEX61rr5aLxxdplLHWLcQ@@4], subscription=[sub-1]}
		        	//logger.info(accessor.getNativeHeader("message-id").get(0));
		        	break;
		        case NACK:
		        	// 客户端nack前置处理..
		        	
		        	break;
		        default:
		        	// dosomething ..
		            break;
			}
		}
		return message;
	}

	/**
	 * 客户端入站消息后置处理
	 */
	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && accessor.getCommand() != null) {
			switch (accessor.getCommand()) {
				case CONNECT:
					// 用户请求连接后置处理..
					
					break;
				case DISCONNECT:
					// 用户请求断开连接后置处理..
					// In some cases this event may be published more than once per session. 
					// Components should be idempotent with regard to multiple disconnect events.
					if (accessor.getUser() != null) {
						logger.info(accessor.getUser() + " disconnected ...");
					}
					break;
				case SUBSCRIBE:
					// 用户请求订阅后置处理..
					if(accessor.getUser() != null) {
						logger.info(accessor.getUser() + " subscribe " + accessor.getDestination() + " succeed ...");
//						simpMessagingTemplate.convertAndSendToUser("/topic/msg","订阅消息发送成功");
					}
					break;
		        case UNSUBSCRIBE:
		        	// 用户取消订阅请求后置处理..防止用户订阅不合法频道..
		        	
		        	break;
		        case SEND:
		        	// 用户发送信息后置处理..
		        	if(accessor.getUser() != null) {
		        		logger.info(accessor.getUser() + "send msg post-processing...");
		        	}
		        	break;
		        case ACK:
		        	// 客户端确认ack后置处理..
		        	
		        	break;
		        case NACK:
		        	// 客户端确认nack后置处理..
		        	
		        	break;
				default:
					// dosomething ..
					break;
			}
		}
	}
	
    /**
     * 解析token并获取认证用户信息
     * 
     * @param token
     * @return
     */
    private Principal verifyToken(String token) {
    	if(true) {
    		return new ClientAuthInfo(token);
    	}
    	return null;
    }
    
    /**
     * 配置用户订阅地址: 自定义队列持久化并通过rabbitmq发送持久化消息以实现持久化订阅
     * 
     * @param accessor
     */
	private void configSubDest(StompHeaderAccessor accessor) {
		// 获取订阅路径
		String subDest = accessor.getDestination();
		// 校验订阅目的地
		String subPrefix = verifySubDest(subDest);
		if(StringUtils.isBlank(subPrefix)) {
			throw new RuntimeException("Illegal subscription path '" + subDest + "'");
		}
		// 排除不需要拦截的订阅前缀
		if(excludePrefixSet.contains(subPrefix)) {
			return;
		}
		boolean isOperSub = operSubPrefix.equals(subPrefix);
		boolean isUserSub = userSubPrefix.equals(subPrefix);
		// 拦截其他非自定义前缀
		if (!isOperSub && !isUserSub) {
			throw new RuntimeException("Illegal subscription path '" + subDest + "'");
		}
		String userId = accessor.getUser().getName();// 获取用户标识
		String subSuffix = subDest.substring(subPrefix.length());
		String queueName = subPrefix.substring(1, subPrefix.length() - 1) + "-" + userId;
		String routingKey = isOperSub ? subSuffix : subSuffix + "." + userId;
		String exchange = isOperSub ? operExchange : userExchange;
		/**
		 * To-Do:
		 * 1.每次订阅都会增加一个通道连接，当并发量很大时是否存在问题？
		 * 2.持久化队列中允许缓存的未被确认的消息数量设置多少合适?
		 */
		// 基于routingkey以及queueName创建一个持久化订阅队列
		try {
			Channel channel = connectionFactory.createConnection().createChannel(false);
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-message-ttl", 60000);// 设置队列消息过期时间(ms)
			args.put("x-expires", 1800000);// 设置队列过期时间(ms)
			channel.queueDeclare(queueName, true, false, false, null);// 第5个参数设置队列属性
			channel.basicQos(5, false);// 设置每个消费者每次抓取的未ack消息最大数量(只有在stomp客户端设置为ack应答有效)
			channel.queueBind(queueName, exchange, routingKey);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		/**
		 * 说明: 例如设置的实际订阅地址为: /amq/queue/msg 创建的持久化queue为msg并且通过routingkey绑定到交换机
		 * 那么有两种方式将消息发送该队列:
		 * 1.通过spring stomp websocket提供的API发送到指定队列(消息并未持久化?)
		 * 指定目的地方式:simpMessagingTemplate.convertAndSend("/amq/queue/msg", serverMessage);
		 * 指定交换机方式:simpMessagingTemplate.convertAndSend("/topic/{routingkey}", serverMessage);
		 * 2.通过rabbitmq提供的接口将消息发送到该queue绑定的交换机(可设置消息持久化等，通过spring rabbitTemplate发送则默认持久化)
		 * rabbitTemplate.convertAndSend("amq.topic", "routingkey", JsonUtils.toJson(serverMessage));
		 */
		// 设置实际的订阅地址: mq代理前缀 + 队列名称
		accessor.setDestination(brokerPrefix + queueName);
	}
	
	/**
	 * 订阅目的地合法性校验
	 * 
	 * @param subDest 订阅地址
	 * 
	 * @return 订阅前缀
	 */
	private String verifySubDest(String subDest) {
		// 校验订阅路径
		if (StringUtils.isBlank(subDest) || !subDest.startsWith("/")) {
			return "";
		}
		// 获取订阅前缀
		int prefixEnd = subDest.indexOf("/", 1);
		if (prefixEnd == -1) {
			prefixEnd = subDest.length() - 1;
		}
		return subDest.substring(0, prefixEnd + 1);
	}
	
}
