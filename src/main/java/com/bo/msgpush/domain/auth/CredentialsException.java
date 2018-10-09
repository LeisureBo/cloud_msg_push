package com.bo.msgpush.domain.auth;

/**
 * Exception thrown due to a problem with the credential(s) submitted for an
 * account during the authentication process.
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:49:34
 */
public class CredentialsException extends AuthenticationException {
	
	/**
     * Creates a new CredentialsException.
     */
    public CredentialsException() {
        super();
    }

    /**
     * Constructs a new CredentialsException.
     *
     * @param message the reason for the exception
     */
    public CredentialsException(String message) {
        super(message);
    }

    /**
     * Constructs a new CredentialsException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public CredentialsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new CredentialsException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public CredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CredentialsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
    
}
