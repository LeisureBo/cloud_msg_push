/**
 * 
 */
package com.bo.msgpush.config;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @notes
 * 
 * @author wangboc
 * 
 * @version 2018年7月9日 下午3:50:51
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	@Value("${server.port}")
	private String port;
	
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/websocket").setViewName("websocket");
		registry.addViewController("/stomp").setViewName("stomp");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	/**
	 * 跨域过滤器
	 *
	 * @return
	 */
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		System.out.println("cors filter from " + port);
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}
	
	/**
	 * @note 替代默认的ObjectMapper(全局配置)
	 * 
	 * @return
	 */
	@Bean
	@Primary
	public ObjectMapper customMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// 设置时区
		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		// 设置序列化日期格式
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// 设置null值不参与序列化(字段不被显示) 仅对pojo有效 map list无效
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		// 禁用空对象转换json校验
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 禁用未知字段校验
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 是否忽略transient注解的字段 (默认为false)
		objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
		return objectMapper;
	}
	
}
