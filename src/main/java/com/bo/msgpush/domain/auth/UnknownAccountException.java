package com.bo.msgpush.domain.auth;

/**
 * Thrown when attempting to authenticate with a principal that doesn't exist in the system (e.g.
 * by specifying a username that doesn't relate to a user account).
 *
 * Whether or not an application wishes to alert a user logging in to the system of this fact is
 * at the discretion of those responsible for designing the view and what happens when this
 * exception occurs. 
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:35:57
 */
public class UnknownAccountException extends AccountException {
	
	/**
     * Creates a new UnknownAccountException.
     */
    public UnknownAccountException() {
        super();
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     */
    public UnknownAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public UnknownAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public UnknownAccountException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public UnknownAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
    
}
