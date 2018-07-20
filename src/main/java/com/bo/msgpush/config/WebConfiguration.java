/**
 * 
 */
package com.bo.msgpush.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
	
}
