package com.bo.msgpush.domain.auth;

import java.io.Serializable;

/**
 * AuthenticationInfo represents a Subject's (aka user's) stored account
 * information relevant to the authentication/log-in process only.
 * 
 * It is important to understand the difference between this interface and the
 * {@link AuthenticationToken AuthenticationToken} interface. AuthenticationInfo
 * implementations represent already-verified and stored account data, whereas
 * an AuthenticationToken represents data submitted for any given login attempt
 * (which may or may not successfully match the verified and stored account
 * AuthenticationInfo).
 * 
 * @author wangboc
 * @version 2018年9月30日 下午5:04:15
 */
public interface AuthenticationInfo extends Serializable {

    /**
     * Returns the principal associated with the corresponding Client. The principal is an identifying piece of
     * information useful to the application such as a username, or user id, a given name, etc - anything useful
     * to the application to identify the current Client.
     * 
     * @return the principal associated with the corresponding Client.
     */
    Object getPrincipal();

    /**
     * Returns the credentials associated with the corresponding Client. A credential verifies the
     * {@link #getPrincipal() principal} associated with the Client, such as a password or private key. Credentials
     * are used by the Authenticator particularly during the authentication process to ensure that submitted credentials
     * during a login attempt match exactly the credentials here in the AuthenticationInfo instance.
     *
     * @return the credentials associated with the corresponding Client.
     */
    Object getCredentials();
}
