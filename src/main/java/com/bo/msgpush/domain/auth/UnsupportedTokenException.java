package com.bo.msgpush.domain.auth;

/**
 * Exception thrown during the authentication process when an
 * {@link com.bo.msgpush.domain.auth.AuthenticationToken} implementation is not supported
 * 
 * @author wangboc
 * 
 * @version 2018年10月8日 下午1:23:11
 */
public class UnsupportedTokenException extends AuthenticationException {
	
	/**
	 * Creates a new UnsupportedTokenException.
	 */
	public UnsupportedTokenException() {
		super();
	}

	/**
	 * Constructs a new UnsupportedTokenException.
	 *
	 * @param message the reason for the exception
	 */
	public UnsupportedTokenException(String message) {
		super(message);
	}

	/**
	 * Constructs a new UnsupportedTokenException.
	 *
	 * @param cause the underlying Throwable that caused this exception to be thrown.
	 */
	public UnsupportedTokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new UnsupportedTokenException.
	 *
	 * @param message the reason for the exception
	 * @param cause the underlying Throwable that caused this exception to be thrown.
	 */
	public UnsupportedTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public UnsupportedTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
