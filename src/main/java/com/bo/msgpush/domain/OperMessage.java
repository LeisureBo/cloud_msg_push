/**
 * 
 */
package com.bo.msgpush.domain;

/**
 * @notes 运营消息
 * 
 * @author wangboc
 * 
 * @version 2018年7月20日 下午2:39:07
 */
public class OperMessage extends AbsMessage {

	private static final long serialVersionUID = -7118459383016570252L;

	/** 用户可见 (default=False) */
	private boolean visible;
	
	/** 消息路由 (required=True) */
	private String routingKey;

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

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
		return "OperMessage [visible=" + visible + ", routingKey=" + routingKey + "]";
	}

}
