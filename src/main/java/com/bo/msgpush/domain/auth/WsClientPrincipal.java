package com.bo.msgpush.domain.auth;

import java.security.Principal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


/**
 * @notes 用于stomp保存用户认证信息 {@link org.springframework.messaging.simp.SimpMessageHeaderAccessor#setUser()}
 * 
 * @Author wangboc
 * 
 * @Version 2018年6月28日　上午11:23:50
 */
// @JsonAutoDetect注解用于com.fasterxml.jackson序列化与反序列化设置属性自动检测规则
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class WsClientPrincipal implements Principal, java.io.Serializable {

	private static final long serialVersionUID = 2978584580328464624L;
	
	/**
     * The principal's name
     *
     * @serial
     */
	private String clientId;// 客户端标识
	
	/**
	 * The empty constructor
	 */
	public WsClientPrincipal() {
		super();
	}
	
	/**
	 * Creates a principal.
	 *  
	 * @param clientId The principal's string name.
     * @exception NullPointerException If the name is null.
	 */
	public WsClientPrincipal(String clientId) {
		if (clientId == null) {
            throw new NullPointerException("null clientId is illegal");
        }
		this.clientId = clientId;
	}

	@Override
	public String getName() {
		return this.clientId;
	}
	
    /**
     * Returns a hash code for this principal.
     *
     * @return The principal's hash code.
     */
	@Override
	public int hashCode() {
		return clientId.hashCode();
	}
	
	/**
     * Compares this principal to the specified object.
     *
     * @param object The object to compare this principal against.
     * @return true if they are equal; false otherwise.
     */
	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WsClientPrincipal) {
            return clientId.equals(((WsClientPrincipal)obj).getName());
        }
        return false;
	}

	/**
     * Returns a string representation of this principal.
     *
     * @return The principal's name.
     */
	@Override
	public String toString() {
		return "WsClientPrincipal [clientId=" + clientId + "]";
	}
	
}
