package com.game.core.dto;

import org.apache.mina.core.session.IoSession;

public class OnlineUserDto {
	public static final String ACTION_LOGIN = "login"; 
	public static final String ACTION_LOGOUT = "logout"; 
	public static final String ACTION_GAME_START = "game-start"; 
	public static final String ACTION_FAST_JOIN = "fast-join"; 
	public static final String ACTION_FORWARD = "forward"; 
	public static final String ACTION_GET_FRIENDLIST = "getFriendList"; 
	public static final String ACTION_INVITE = "invite"; 
	public static final String ACTION_SYSTEM_BROADCAST = "system-broadcast"; 
	
	
	public static final String STATUS_ONLINE = "online"; 
	public static final String STATUS_OFFLINE = "offline"; 
	public static final String STATUS_PLAYING = "playing"; 
	public static final String STATUS_IN_ROOM = "in_room"; 
	
	
	String roomId;
	
	String username;
	
	String status;
	
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


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OnlineUserDto other = (OnlineUserDto) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
	
	
}
