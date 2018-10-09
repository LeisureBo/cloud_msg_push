package com.bo.msgpush.domain.auth;

/**
 * Interface representing account information that may use a salt when hashing
 * credentials. This interface exists primarily to support environments that
 * hash user credentials (e.g. passwords).
 * 
 * Salts should typically be generated from a secure pseudo-random number
 * generator so they are effectively impossible to guess. The salt value should
 * be safely stored along side the account information to ensure it is
 * maintained along with the account's credentials.
 * 
 * @author wangboc
 * @version 2018年9月30日 下午5:54:31
 */
public interface SaltedAuthenticationInfo extends AuthenticationInfo {
	
    /**
     * Returns the salt used to salt the account's credentials or {@code null} if no salt was used.
     *
     * @return the salt used to salt the account's credentials or {@code null} if no salt was used.
     */
    String getCredentialsSalt();
	
}
