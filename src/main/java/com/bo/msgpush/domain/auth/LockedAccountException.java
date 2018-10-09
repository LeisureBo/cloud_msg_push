package com.bo.msgpush.domain.auth;

/**
 * A special kind of DisabledAccountException, this exception is thrown when attempting
 * to authenticate and the corresponding account has been disabled explicitly due to being locked.
 *
 * For example, an account can be locked if an administrator explicitly locks an account or
 * perhaps an account can be locked automatically by the system if too many unsuccessful
 * authentication attempts take place during a specific period of time (perhaps indicating a
 * hacking attempt).
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:45:53
 */
public class LockedAccountException extends AccountException {
	
	/**
     * Creates a new LockedAccountException.
     */
    public LockedAccountException() {
        super();
    }

    /**
     * Constructs a new LockedAccountException.
     *
     * @param message the reason for the exception
     */
    public LockedAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new LockedAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public LockedAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new LockedAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public LockedAccountException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public LockedAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
    
}
