/**
 * 
 */
package com.bo.msgpush.domain.msg;

import java.io.Serializable;

/**
 * @notes 消息体
 * 
 * @author wangboc
 * 
 * @version 2018年7月20日 下午2:28:13
 */
public class MessageBody implements Serializable {

	private static final long serialVersionUID = -5812723234748335200L;
	
	/** 消息标题 (required=True)*/
	private String title;
	
	/** 消息内容 (required=True) */
	private String content;
	
	/** 图片地址 */
	private String img;
	
	/** 跳转链接 */
	private String url;
	
	/** 其他扩展 */
	private String remark;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}

	/**
	 * @param img
	 *            the img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageBody [title=" + title + ", content=" + content + ", img=" + img + ", url=" + url + ", remark=" + remark + "]";
	}

}
