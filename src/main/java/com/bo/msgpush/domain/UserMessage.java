/**
 * 
 */
package com.bo.msgpush.domain;

/**
 * @notes 用户消息
 * 
 * @author wangboc
 * 
 * @version 2018年7月20日 下午2:39:57
 */
public class UserMessage extends AbsMessage {

	private static final long serialVersionUID = -1252504102962418987L;

	/** 来源用户标识(required=True) */
	private String originUid;

	/** 目标用户标识(required=True) */
	private String targetUid;

	/** 是否未读(default=True) */
	private boolean unread = Boolean.TRUE;

	/**
	 * @return the originUid
	 */
	public String getOriginUid() {
		return originUid;
	}

	/**
	 * @param originUid
	 *            the originUid to set
	 */
	public void setOriginUid(String originUid) {
		this.originUid = originUid;
	}

	/**
	 * @return the targetUid
	 */
	public String getTargetUid() {
		return targetUid;
	}

	/**
	 * @param targetUid
	 *            the targetUid to set
	 */
	public void setTargetUid(String targetUid) {
		this.targetUid = targetUid;
	}

	/**
	 * @return the unread
	 */
	public boolean isUnread() {
		return unread;
	}

	/**
	 * @param unread
	 *            the unread to set
	 */
	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserMessage [originUid=" + originUid + ", targetUid=" + targetUid + ", unread=" + unread + "]";
	}

}
