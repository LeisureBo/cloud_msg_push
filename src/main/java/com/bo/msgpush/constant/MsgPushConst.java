package com.bo.msgpush.constant;

/**
 * @notes 常量
 * 
 * @author wangboc
 * 
 * @version 2018年11月29日 下午3:19:48
 */
public interface MsgPushConst {
	
	enum OnlineStatus {
		
		OFFLINE(0, "离线"), ONLINE(1, "在线");
		
		OnlineStatus(Integer value, String desc) {
			this.value = value;
			this.desc = desc;
		}
		
		final Integer value;// 值
		
		final String desc;// 描述
		
		public Integer value() {
			return value;
		}

		public String desc() {
			return desc;
		}
	}
	
}
