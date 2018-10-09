package com.bo.msgpush.domain.auth;

/**
 * Thrown when attempting to authenticate with credential(s) that do not match the actual
 * credentials associated with the account principal.
 *
 * For example, this exception might be thrown if a user's password is "secret" and
 * "secrets" was entered by mistake.
 *
 * Whether or not an application wishes to let
 * the user know if they entered incorrect credentials is at the discretion of those
 * responsible for defining the view and what happens when this exception occurs.
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:54:32
 */
public class IncorrectCredentialsException extends CredentialsException {

    /**
     * Creates a new IncorrectCredentialsException.
     */
    public IncorrectCredentialsException() {
        super();
    }

    /**
     * Constructs a new IncorrectCredentialsException.
     *
     * @param message the reason for the exception
     */
    public IncorrectCredentialsException(String message) {
        super(message);
    }

    /**
     * Constructs a new IncorrectCredentialsException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public IncorrectCredentialsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new IncorrectCredentialsException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public IncorrectCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public IncorrectCredentialsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
    
}
