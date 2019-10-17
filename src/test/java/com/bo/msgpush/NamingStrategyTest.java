package com.bo.msgpush;

import org.junit.Test;
import org.springframework.amqp.core.AnonymousQueue.Base64UrlNamingStrategy;
import org.springframework.amqp.core.AnonymousQueue.UUIDNamingStrategy;

/**
 * @Description 
 * @Author Bo
 * @Version 2019年3月21日　下午5:02:02
 * @码云 https://gitee.com/LeisureBo
 */
public class NamingStrategyTest {
	
	@Test
	public void test01() {
		
		String base64Name = Base64UrlNamingStrategy.DEFAULT.generateName();
		
		String uuidName = UUIDNamingStrategy.DEFAULT.generateName();
		
		System.out.println(base64Name);
		
		System.out.println(uuidName);
	}
	
}
