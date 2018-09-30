/**
 * 
 */
package com.bo.msgpush.domain.msg;

import java.io.Serializable;

/**
 * @notes 测试消息模型
 * 
 * @author wangboc
 * 
 * @version 2018年7月6日 下午3:10:03
 */
public class ClientMessage implements Serializable {

	private static final long serialVersionUID = -3762030427845178224L;

	private String type;

	private String fromUserId;

	private String toUserId;

	private String message;

	
	public ClientMessage() {
		super();
	}

	public ClientMessage(String message) {
		this.message = message;
	}
	
	public ClientMessage(String type, String message) {
		this.type = type;
		this.message = message;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the fromUserId
	 */
	public String getFromUserId() {
		return fromUserId;
	}

	/**
	 * @param fromUserId
	 *            the fromUserId to set
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
	 * @param toUserId
	 *            the toUserId to set
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
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ClientMessage [type=" + type + ", fromUserId=" + fromUserId + ", toUserId=" + toUserId + ", message="
				+ message + "]";
	}

}
