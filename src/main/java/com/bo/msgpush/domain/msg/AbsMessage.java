/**
 * 
 */
package com.bo.msgpush.domain.msg;

import java.io.Serializable;

/**
 * @notes 消息模型抽象
 * 
 * @author wangboc
 * 
 * @version 2018年7月20日 下午2:27:13
 */
public abstract class AbsMessage implements Serializable {

	private static final long serialVersionUID = 88720203508085450L;

	/** 消息头 */
	private MessageHeader msgHeader;

	/** 与H5约定的标签(required=True) */
	private String msgTag;

	/** 与app约定的消息类别，app根据此字段来决定是否识别消息、从msg_body读取哪些字段 (required=True) */
	private String msgType;

	/** 消息体 */
	private MessageBody msgBody;

	/**
	 * @return the msgHeader
	 */
	public MessageHeader getMsgHeader() {
		return msgHeader;
	}

	/**
	 * @param msgHeader
	 *            the msgHeader to set
	 */
	public void setMsgHeader(MessageHeader msgHeader) {
		this.msgHeader = msgHeader;
	}

	/**
	 * @return the msgTag
	 */
	public String getMsgTag() {
		return msgTag;
	}

	/**
	 * @param msgTag
	 *            the msgTag to set
	 */
	public void setMsgTag(String msgTag) {
		this.msgTag = msgTag;
	}

	/**
	 * @return the msgBody
	 */
	public MessageBody getMsgBody() {
		return msgBody;
	}

	/**
	 * @param msgBody
	 *            the msgBody to set
	 */
	public void setMsgBody(MessageBody msgBody) {
		this.msgBody = msgBody;
	}

	/**
	 * @return the msgType
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType
	 *            the msgType to set
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbsMessage [msgHeader=" + msgHeader + ", msgTag=" + msgTag + ", msgType=" + msgType + ", msgBody=" + msgBody + "]";
	}

}
