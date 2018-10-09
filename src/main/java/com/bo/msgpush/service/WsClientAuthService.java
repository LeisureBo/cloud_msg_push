package com.bo.msgpush.service;

import com.bo.msgpush.domain.auth.AuthenticationException;
import com.bo.msgpush.domain.auth.AuthenticationInfo;
import com.bo.msgpush.domain.auth.AuthenticationToken;

/**
 * @notes websocket 客户端连接认证服务
 * 
 * @author wangboc
 * 
 * @version 2018年9月30日 下午2:13:28
 */
public interface WsClientAuthService {
	
	/**
	 * 执行websocket客户端连接认证
	 * 
	 * @param clientAuthInfo
	 * @return
	 */
	AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException;
}
