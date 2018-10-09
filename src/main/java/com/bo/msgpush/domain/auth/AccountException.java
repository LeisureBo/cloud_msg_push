package com.bo.msgpush.domain.auth;

/**
 * Exception thrown due to a problem with the account
 * under which an authentication attempt is being executed.
 *
 * @author wangboc
 * 
 * @version 2018年10月8日 下午13:30:30
 */
public class AccountException extends AuthenticationException {

    /**
     * Creates a new AccountException.
     */
    public AccountException() {
        super();
    }

    /**
     * Constructs a new AccountException.
     *
     * @param message the reason for the exception
     */
    public AccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new AccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public AccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new AccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public AccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
