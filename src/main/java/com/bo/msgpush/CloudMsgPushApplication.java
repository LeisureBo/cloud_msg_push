package com.bo.msgpush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CloudMsgPushApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudMsgPushApplication.class, args);
	}
}
