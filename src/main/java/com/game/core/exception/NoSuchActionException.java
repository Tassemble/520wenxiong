package com.game.core.exception;

/**
 * @author CHQ
 * @since 2013-7-28
 */
public class NoSuchActionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 932618008296218478L;

	int code;
	
	String message;
	String action;
	
	public NoSuchActionException(int code, String message, String action) {
		super(message);
		this.code = code;
		this.message = message;
		this.action = action;
	}
	
	
	public NoSuchActionException(String message, String action) {
		super(message);
		this.code = ExceptionConstant.NO_AUTHENTICATION_CODE;
		this.message = message;
		this.action = action;
	}
	
	
	public NoSuchActionException(String action) {
		this.code = ExceptionConstant.NO_AUTHENTICATION_CODE;
		this.action = action;
	}
}
