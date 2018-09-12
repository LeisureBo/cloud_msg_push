/**
 * 
 */
package com.bo.msgpush.domain;

/**
 * @notes 广播消息
 * 
 * @author wangboc
 * 
 * @version 2018年9月12日 下午3:11:04
 */
public class TopicMessage extends AbsMessage {
	
	private static final long serialVersionUID = -2566675620957973935L;
	
	/** 消息路由 (required=True) */
	private String routingKey;

	/**
	 * @return the routingKey
	 */
	public String getRoutingKey() {
		return routingKey;
	}

	/**
	 * @param routingKey the routingKey to set
	 */
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TopicMessage [routingKey=" + routingKey + ", toString()=" + super.toString() + "]";
	}
	
}
