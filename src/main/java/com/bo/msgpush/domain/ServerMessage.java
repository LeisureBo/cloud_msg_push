/**
 * 
 */
package com.bo.msgpush.domain;

import java.io.Serializable;

/**
 * @notes 服务端消息定义
 * 
 * @author wangboc
 * 
 * @version 2018年7月7日 下午2:20:24
 */
public class ServerMessage implements Serializable {

	private static final long serialVersionUID = -1347167274694428128L;

	private String type;

	private String sourceId;

	private String destId;

	private String message;

	/**
	 * @param message
	 */
	public ServerMessage(String message) {
		super();
		this.message = message;
	}

	/**
	 * @param type
	 * @param message
	 */
	public ServerMessage(String type, String sourceId, String destId, String message) {
		super();
		this.type = type;
		this.sourceId = sourceId;
		this.destId = destId;
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
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId
	 *            the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the destId
	 */
	public String getDestId() {
		return destId;
	}

	/**
	 * @param destId
	 *            the destId to set
	 */
	public void setDestId(String destId) {
		this.destId = destId;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerMessage [type=" + type + ", sourceId=" + sourceId + ", destId=" + destId + ", message=" + message + "]";
	}

}
