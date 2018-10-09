package com.bo.msgpush.domain.auth;

import java.io.Serializable;

/**
 * An AuthenticationToken is a consolidation of an account's principals and
 * supporting credentials submitted by a user during an authentication attempt.
 * 
 * The token is submitted to an Authenticator via the {authenticate(token)}
 * method. The Authenticator then executes the authentication/log-in process.
 * 
 * Common implementations of an AuthenticationToken would have username/password
 * pairs, X.509 Certificate, PGP key, or anything else you can think of. The
 * token can be anything needed by an Authenticator to authenticate properly.
 * 
 * Because applications represent user data and credentials in different ways,
 * implementations of this interface are application-specific. You are free to
 * acquire a user's principals and credentials however you wish (e.g. web form,
 * Swing form, fingerprint identification, etc) and then submit them to the
 * authenticate framework in the form of an implementation of this interface.
 * 
 * @author wangboc
 * @version 2018年9月30日 下午2:39:33
 */
public interface AuthenticationToken extends Serializable {
	
    /**
     * Returns the account identity submitted during the authentication process.
     * <p/>
     * <p>Most application authentications are username/password based and have this
     * object represent a username.  If this is the case for your application,
     * take a look at the {@link UsernamePasswordToken UsernamePasswordToken}, as it is probably
     * sufficient for your use.
     * <p/>
     * <p>Ultimately, the object returned is application specific and can represent
     * any account identity (user id, X.509 certificate, etc).
     *
     * @return the account identity submitted during the authentication process.
     * @see UsernamePasswordToken
     */
    Object getPrincipal();

    /**
     * Returns the credentials submitted by the user during the authentication process that verifies
     * the submitted {@link #getPrincipal() account identity}.
     * <p/>
     * <p>Most application authentications are username/password based and have this object
     * represent a submitted password.  If this is the case for your application,
     * take a look at the {@link UsernamePasswordToken UsernamePasswordToken}, as it is probably
     * sufficient for your use.
     * <p/>
     * <p>Ultimately, the credentials Object returned is application specific and can represent
     * any credential mechanism.
     *
     * @return the credential submitted by the user during the authentication process.
     */
    Object getCredentials();
}
