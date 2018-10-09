package com.bo.msgpush.domain.auth;

/**
 * Thrown when a system is configured to only allow a certain number of authentication attempts
 * over a period of time and the current session has failed to authenticate successfully within
 * that number.  
 * 
 * The resulting action of such an exception is application-specific, but most systems either 
 * temporarily or permanently lock that account to prevent further attempts.
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:39:46
 */
public class ExcessiveAttemptsException extends AccountException{
	
	/**
     * Creates a new ExcessiveAttemptsException.
     */
    public ExcessiveAttemptsException() {
        super();
    }

    /**
     * Constructs a new ExcessiveAttemptsException.
     *
     * @param message the reason for the exception
     */
    public ExcessiveAttemptsException(String message) {
        super(message);
    }

    /**
     * Constructs a new ExcessiveAttemptsException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public ExcessiveAttemptsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ExcessiveAttemptsException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public ExcessiveAttemptsException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ExcessiveAttemptsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
    
}
