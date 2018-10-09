package com.bo.msgpush.domain.auth;

/**
 * A {@code HostAuthenticationToken} retains the host information from where
 * an authentication attempt originates.
 * 
 * @author wangboc
 * @version 2018年9月30日 下午4:33:47
 */
public interface HostAuthenticationToken extends AuthenticationToken {
	
    /**
     * Returns the host name of the client from where the
     * authentication attempt originates or if the project environment cannot or
     * chooses not to resolve the hostname to improve performance, this method
     * returns the String representation of the client's IP address.
     * <p/>
     * When used in web environments, this value is usually the same as the
     * {@code ServletRequest.getRemoteHost()} value.
     *
     * @return the fully qualified name of the client from where the
     *         authentication attempt originates or the String representation
     *         of the client's IP address is hostname resolution is not
     *         available or disabled.
     */
    String getHost();
}
