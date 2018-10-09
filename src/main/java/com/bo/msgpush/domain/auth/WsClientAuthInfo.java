package com.bo.msgpush.domain.auth;

import java.security.Principal;

/**
 * @notes 客户端连接认证信息
 * 
 * @author wangboc
 * 
 * @version 2018年9月30日 下午5:59:23
 */
public class WsClientAuthInfo implements SaltedAuthenticationInfo {

	private static final long serialVersionUID = -2672034835528521616L;

    /**
     * The principals identifying the account associated with this AuthenticationInfo instance.
     */
    protected Principal principal;
    
    /**
     * The credentials verifying the account principals.
     */
    protected Object credentials;

    /**
     * Any salt used in hashing the credentials.
     */
    protected String credentialsSalt;
	
    
	/**
	 * Default no-argument constructor.
	 */
	public WsClientAuthInfo() {
		super();
	}
	
    /**
     * Constructor that takes in an account's identifying principal(s) and 
     * its corresponding credentials that verify the principals.
     *
     * @param principal  a account's identifying principal(s)
     * @param credentials the account corresponding principal that verify the principal.
     */
	public WsClientAuthInfo(Principal principal, Object credentials) {
		super();
		this.principal = principal;
		this.credentials = credentials;
	}

    /**
     * Constructor that takes in an account's identifying principal(s), hashed credentials 
     * used to verify the principal, and the salt used when hashing the credentials.
     *
     * @param principals        a account's identifying principal(s)
     * @param hashedCredentials the hashed credentials that verify the principals.
     * @param credentialsSalt   the salt used when hashing the hashedCredentials.
     */
	public WsClientAuthInfo(Principal principal, Object hashedCredentials, String credentialsSalt) {
		super();
		this.principal = principal;
		this.credentials = hashedCredentials;
		this.credentialsSalt = credentialsSalt;
	}

	/** (non-Javadoc)
	 * @see com.bo.msgpush.domain.auth.AuthenticationInfo#getPrincipal()
	 */
	@Override
	public Principal getPrincipal() {
		return this.principal;
	}

    /**
     * Sets the identifying principal(s) represented by this instance.
     *
     * @param principal the indentifying attributes of the corresponding account.
     */
	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}
	
	/** (non-Javadoc)
	 * @see com.bo.msgpush.domain.auth.AuthenticationInfo#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return this.credentials;
	}

    /**
     * Sets the credentials that verify the principals/identity of the associated account.
     *
     * @param credentials attribute(s) that verify the account's identity/principals, such as a password or private key.
     */
	public void setCredentials(Object credentials) {
		this.credentials = credentials;
	}
	
	/** (non-Javadoc)
	 * @see com.bo.msgpush.domain.auth.SaltedAuthenticationInfo#getCredentialsSalt()
	 */
	@Override
	public String getCredentialsSalt() {
		// TODO Auto-generated method stub
		return null;
	}
	
    /**
     * Sets the salt used to hash the credentials, or {@code null} if no salt was used or credentials were not
     * hashed at all.
     *
     * @param salt the salt used to hash the credentials, or {@code null} if no salt was used or credentials were not
     * 		  hashed at all.
     */
	public void setCredentialsSalt(String credentialsSalt) {
		this.credentialsSalt = credentialsSalt;
	}
	
    /**
     * Returns true if the Object argument is an instanceof WsClientAuthInfo and
     * its {@link #getPrincipal() principal} are equal to this instance's principal, false otherwise.
     *
     * @param o the object to compare for equality.
     * @return true if the Object argument is an instanceof WsClientAuthInfo and
     *         its {@link #getPrincipal() principal} are equal to this instance's principal, false otherwise.
     */
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WsClientAuthInfo)) return false;

        WsClientAuthInfo that = (WsClientAuthInfo) obj;

        //noinspection RedundantIfStatement
        if (principal != null ? !principal.equals(that.principal) : that.principal != null) return false;

        return true;
    }

    /**
     * Returns the hashcode of the internal {@link #getPrincipal() principal} instance.
     *
     * @return the hashcode of the internal {@link #getPrincipal() principal} instance.
     */
    @Override
    public int hashCode() {
        return (principal != null ? principal.hashCode() : 0);
    }

    /**
     * Simple implementation that merely returns {@link #getPrincipal()}.toString()
     *
     * @return <code>{@link #getPrincipals() principals}.toString()</code>
     */
    @Override
    public String toString() {
        return principal.toString();
    }
}
