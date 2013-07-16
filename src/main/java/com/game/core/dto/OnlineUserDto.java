package com.game.core.dto;

import org.apache.mina.core.session.IoSession;

public class OnlineUserDto {
	public static final String ACTION_LOGIN = "login"; 
	public static final String ACTION_LOGOUT = "logout"; 
	public static final String ACTION_GAME_START = "game-start"; 
	public static final String ACTION_FAST_JOIN = "fast-join"; 
	public static final String ACTION_FORWARD = "forward"; 
	public static final String ACTION_GET_FRIENDLIST = "login"; 
	public static final String ACTION_INVITE = "invite"; 
	
	
	public static final int STATUS_ONLINE = 1; 
	public static final int STATUS_OFFLINE = 2; 
	public static final int STATUS_PLAYING = 3; 
	public static final int STATUS_IN_ROOM = 4; 
	
	
	String roomId;
	
	String username;
	
	int status;
	
	IoSession session;
	
	

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
