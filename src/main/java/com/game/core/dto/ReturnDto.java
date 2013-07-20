package com.game.core.dto;

public class ReturnDto {
	public static final int ALREADY_LOGON_CODE = -100;
	public static final int ALREADY_LOGON_CODE_WITH_OTHER_REMOTE_CLIENT = -101;
	
	
	
	String action;
	int code;
	String message;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ReturnDto(int code, String action, String message) {
		super();
		this.action = action;
		this.code = code;
		this.message = message;
	}
	public ReturnDto(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	
	
	
	
	
	
	
}
