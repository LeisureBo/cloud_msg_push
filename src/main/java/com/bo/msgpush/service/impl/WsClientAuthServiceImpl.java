package com.bo.msgpush.service.impl;

import java.security.Principal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bo.msgpush.domain.auth.AuthenticationException;
import com.bo.msgpush.domain.auth.AuthenticationInfo;
import com.bo.msgpush.domain.auth.AuthenticationToken;
import com.bo.msgpush.domain.auth.UnknownAccountException;
import com.bo.msgpush.domain.auth.UnsupportedTokenException;
import com.bo.msgpush.domain.auth.WsClientAuthInfo;
import com.bo.msgpush.domain.auth.WsClientAuthToken;
import com.bo.msgpush.domain.auth.WsClientPrincipal;
import com.bo.msgpush.service.WsClientAuthService;

/**
 * @notes websocket客户端连接认证实现
 * 
 * @author wangboc
 * 
 * @version 2018年9月30日 下午7:01:30
 */
@Service("wsClientAuthService")
public class WsClientAuthServiceImpl implements WsClientAuthService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private RestTemplate restTemplate;
	
	@Value("${push.auth.url}")
	private String clientAuthUrl;

	/** (non-Javadoc)
	 * @see com.bo.msgpush.service.WsClientAuthService#doAuthenticate(com.bo.msgpush.domain.auth.WsClientAuthInfo)
	 */
	@Override
	public AuthenticationInfo doAuthenticate(AuthenticationToken authToken) throws AuthenticationException {
		// 判断token是否合法
		if(!(authToken instanceof WsClientAuthToken)) {
			String msg = "The submitted AuthenticationToken [" + authToken + "] is not supported";
			logger.error(msg);
			throw new UnsupportedTokenException(msg);
		}
		// 获取认证信息
		AuthenticationInfo authenticationInfo = null;
		try {
			authenticationInfo = restTemplate.postForObject(clientAuthUrl, authToken, WsClientAuthInfo.class);
		} catch (Exception e) {
			logger.error("Failed to get remote authentication information: {}", e.getMessage());
			// 模拟远程获取的认证信息
			WsClientPrincipal principal = new WsClientPrincipal((String) authToken.getPrincipal());
			String credentials = (String) authToken.getCredentials();
			authenticationInfo = new WsClientAuthInfo(principal, credentials);
		}
		if(authenticationInfo == null || !(authenticationInfo instanceof WsClientAuthInfo)) {
			String msg = "Unable to find account data for the " +
                    "submitted AuthenticationToken [" + authToken + "].";
            logger.error(msg);
			throw new UnknownAccountException(msg);
		}
		return authenticationInfo;
	}
	
}
