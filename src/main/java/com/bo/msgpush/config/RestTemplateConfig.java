package com.bo.msgpush.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @notes Restful配置
 * 
 * @author wangboc
 * 
 * @version 2018年6月15日 下午5:14:05
 */
@Configuration
public class RestTemplateConfig {

	@Value("${rest.maxTotalConnection:200}")
	private int maxTotalConnection;// 连接池的最大连接数默认为0

	@Value("${rest.maxConnectionPerRoute:30}")
	private int maxConnectionPerRoute;// 单个主机的最大连接数

	@Value("${rest.retryTimes:3}")
	private int retryTimes;// 重试次数

	@Value("${rest.connTimeout:15000}")
	private int connTimeout;// 连接超时默认15s

	@Value("${rest.readTimeout:15000}")
	private int readTimeout;// 读取超时默认15s

	@Value("${rest.connWaitTimeout:15000}")
	private int connWaitTimeout;// 连接等待超时15s

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(clientHttpRequestFactory());
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
		// 重新设置StringHttpMessageConverter字符集为UTF-8，解决中文乱码问题
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		HttpMessageConverter<?> converterTarget = null;
		for (HttpMessageConverter<?> item : converterList) {
			if (StringHttpMessageConverter.class == item.getClass()) {
				converterTarget = item;
				break;
			}
		}
		if (null != converterTarget) {
			converterList.remove(converterTarget);
		}
		converterList.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		// 加入FastJson转换器 根据使用情况进行操作，此段注释，默认使用jackson
		// converterList.add(new FastJsonHttpMessageConverter4());
		return restTemplate;
	}

//	@Bean
//	public HttpMessageConverters fastjsonConverter() {
//		FastJsonConfig fastJsonConfig = new FastJsonConfig();
//		// 自定义格式化输出
//		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero);
//
//		FastJsonHttpMessageConverter4 fastjson = new FastJsonHttpMessageConverter4();
//		fastjson.setFastJsonConfig(fastJsonConfig);
//		return new HttpMessageConverters(fastjson);
//	}
	
	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}).build();
			httpClientBuilder.setSSLContext(sslContext);
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslConnectionSocketFactory).build();// 注册http和https请求
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);// 开始设置连接池
			poolingHttpClientConnectionManager.setMaxTotal(maxTotalConnection); // 最大连接数200
			poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxConnectionPerRoute); // 同路由并发数20
			httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
			httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(retryTimes, true));// 重试次数
			HttpClient httpClient = httpClientBuilder.build();
			HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);// httpClient连接配置
			clientHttpRequestFactory.setConnectTimeout(connTimeout);// 连接超时
			clientHttpRequestFactory.setReadTimeout(readTimeout);// 数据读取超时时间
			clientHttpRequestFactory.setConnectionRequestTimeout(connWaitTimeout);// 连接不够用的等待时间
			return clientHttpRequestFactory;
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}
		return null;
	}
}
