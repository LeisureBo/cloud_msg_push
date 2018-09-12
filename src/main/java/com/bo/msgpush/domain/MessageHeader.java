/**
 * 
 */
package com.bo.msgpush.domain;

import java.io.Serializable;

/**
 * @notes 消息头
 * 
 * @author wangboc
 * 
 * @version 2018年7月20日 下午3:21:42
 */
public class MessageHeader implements Serializable {

	private static final long serialVersionUID = 3554255881053543039L;

	/** 消息优先级: 0~9的数值，值越大表示权重越高，默认值为4 */
	private int priority; 
	
	/** 消息是否持久化 */
	private boolean persistent;

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the persistent
	 */
	public boolean getPersistent() {
		return persistent;
	}

	/**
	 * @param persistent the persistent to set
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageHeader [priority=" + priority + ", persistent=" + persistent + "]";
	}
	
}
