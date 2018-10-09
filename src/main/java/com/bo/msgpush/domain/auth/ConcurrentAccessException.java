package com.bo.msgpush.domain.auth;

/**
 * Thrown when an authentication attempt has been received for an account that has already been
 * authenticated (i.e. logged-in), and the system is configured to prevent such concurrent access.
 *
 * This is useful when an application must ensure that only one person is logged-in to a single
 * account at any given time.
 *
 * Sometimes account names and passwords are lazily given away
 * to many people for easy access to a system.  Such behavior is undesirable in systems where
 * users are accountable for their actions, such as in government applications, or when licensing
 * agreements must be maintained, such as those which only allow 1 user per paid license.
 *
 * By disallowing concurrent access, such systems can ensure that each authenticated session
 * corresponds to one and only one user at any given time.
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:42:14
 */
public class ConcurrentAccessException extends AccountException {
	
	/**
	 * Creates a new ConcurrentAccessException.
	 */
    public ConcurrentAccessException() {
        super();
    }

    /**
     * Constructs a new ConcurrentAccessException.
     *
     * @param message the reason for the exception
     */
    public ConcurrentAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConcurrentAccessException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public ConcurrentAccessException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ConcurrentAccessException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public ConcurrentAccessException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ConcurrentAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
    
}
