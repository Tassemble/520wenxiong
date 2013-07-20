package com.game.core.dto;

public class OnlineUserVo {
	String roomId;
	
	String username;
	
	String status;

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

	public OnlineUserVo(OnlineUserDto user) {
		this.roomId = user.getRoomId();
		this.username = user.getUsername();
		this.status = user.getStatus();
	}
	
	
}
