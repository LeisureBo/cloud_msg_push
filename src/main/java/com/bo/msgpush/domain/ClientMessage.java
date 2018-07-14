/**
 * 
 */
package com.bo.msgpush.domain;

import java.io.Serializable;

/**
 * @notes 
 * 
 * @author wangboc
 * 
 * @version 2018年7月6日 下午3:10:03
 */
public class ClientMessage implements Serializable{

	private static final long serialVersionUID = -3762030427845178224L;
	
	private String fromUserId;
	
	private String toUserId;
	
	private String message;

	/**
	 * @return the fromUserId
	 */
	public String getFromUserId() {
		return fromUserId;
	}

	/**
	 * @param fromUserId the fromUserId to set
	 */
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	/**
	 * @return the toUserId
	 */
	public String getToUserId() {
		return toUserId;
	}

	/**
	 * @param toUserId the toUserId to set
	 */
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClientMessage [fromUserId=" + fromUserId + ", toUserId=" + toUserId + ", message=" + message + "]";
	}
	
	
}
