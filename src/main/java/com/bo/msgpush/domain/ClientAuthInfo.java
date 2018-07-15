package com.bo.msgpush.domain;

import java.security.Principal;

/**
 * @Description 用于websocket保存用户认证信息
 * @Author 王博
 * @Version 2018年6月28日　上午11:23:50
 * @码云 https://gitee.com/LeisureBo
 */
public class ClientAuthInfo implements Principal {

	private String userId;// 用户标识
	
	
	public ClientAuthInfo(String userId) {
		super();
		this.userId = userId;
	}

	/**
	 * @reeturn 此处返回用户的标识ID
	 */
	@Override
	public String getName() {
		return this.userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "ClientAuthInfo [userId=" + userId + "]";
	}
	
}
