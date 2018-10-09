package com.bo.msgpush.domain.auth;

/**
 * @notes 客户端连接认证token实现
 * 
 * @author wangboc
 * 
 * @version 2018年9月30日 下午4:26:31
 */
public class WsClientAuthToken implements HostAuthenticationToken {

	private static final long serialVersionUID = -3333249553713802304L;

	/**
	 * The client ID
	 */
	private String clientId;

	/**
	 * The client token
	 */
	private String clientToken;

	/**
	 * The location from where the login attempt occurs, or null if not known or
	 * explicitly omitted.
	 */
	private String host;

	/**
	 * Default no-argument constructor.
	 */
	public WsClientAuthToken() {
		super();
	}

	/**
	 * @param clientId
	 * @param clientToken
	 */
	public WsClientAuthToken(String clientId, String clientToken) {
		this(clientId, clientToken, null);
	}

	/**
	 * @param clientId
	 * @param clientToken
	 * @param host
	 */
	public WsClientAuthToken(String clientId, String clientToken, String host) {
		super();
		this.clientId = clientId;
		this.clientToken = clientToken;
		this.host = host;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId
	 *            the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the clientToken
	 */
	public String getClientToken() {
		return clientToken;
	}

	/**
	 * @param clientToken
	 *            the clientToken to set
	 */
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	/**
	 * @param host
	 *        	the host name or IP string from where the attempt is occurring
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns the {@link #getClientId() clientId}.
	 * 
	 * @see com.bo.msgpush.domain.auth.AuthenticationToken#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		return getClientId();
	}

	/**
	 * Returns the {@link #getClientToken() clientToken}.
	 * 
	 * @see com.bo.msgpush.domain.auth.AuthenticationToken#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return getClientToken();
	}

	/**
	 * the host from where the authentication attempt occurs, or null if it is unknown or
     * explicitly omitted.
	 * 
	 * @see com.bo.msgpush.domain.auth.HostAuthenticationToken#getHost()
	 */
	@Override
	public String getHost() {
		return this.host;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClientAuthToken [clientId=" + clientId + ", host=" + host + "]";
	}
	
}
