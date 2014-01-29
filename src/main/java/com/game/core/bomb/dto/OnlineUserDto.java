package com.game.core.bomb.dto;

import org.apache.mina.core.session.IoSession;

import com.game.bomb.domain.User;

public class OnlineUserDto {
	
	public static final String STATUS_ONLINE = "online"; 
	public static final String STATUS_OFFLINE = "offline"; 
	public static final String STATUS_PLAYING = "playing"; 
	public static final String STATUS_IN_ROOM = "in_room"; 
	
	Long  id;
	String roomId;
	String username;
	String status;
	transient IoSession session;
	
	public OnlineUserDto() {}
	public OnlineUserDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
//		this.nickname = user.getNickName();
//		this.heartNum = user.getHeartNum();
//		this.level = user.getLevel();
//		this.loserNum = user.getLoserNum();
//		this.nickname = user.getNickName();
//		this.portrait = user.getPortrait();
//		this.runawayNum = user.getRunawayNum();
//		this.victoryNum = user.getVictoryNum();
//		this.inUse = user.getInUse();
//		this.gold = user.getGold();
//		this.inGot = user.getInGot();
	}
	


	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	
	
}
