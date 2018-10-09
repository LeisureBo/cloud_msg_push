package com.bo.msgpush.domain.auth;

/**
 * Thrown during the authentication process when the system determines the submitted credential(s)
 * has expired and will not allow login.
 *
 * This is most often used to alert a user that their credentials (e.g. password or
 * cryptography key) has expired and they should change the value.  In such systems, the component
 * invoking the authentication might catch this exception and redirect the user to an appropriate
 * view to allow them to update their password or other credentials mechanism.
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:52:34
 */
public class ExpiredCredentialsException extends CredentialsException {
	
	/**
     * Creates a new ExpiredCredentialsException.
     */
    public ExpiredCredentialsException() {
        super();
    }

    /**
     * Constructs a new ExpiredCredentialsException.
     *
     * @param message the reason for the exception
     */
    public ExpiredCredentialsException(String message) {
        super(message);
    }

    /**
     * Constructs a new ExpiredCredentialsException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public ExpiredCredentialsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ExpiredCredentialsException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public ExpiredCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ExpiredCredentialsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
    
}
