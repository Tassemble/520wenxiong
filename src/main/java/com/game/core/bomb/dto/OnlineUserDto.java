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
	String nickname;
	
	Integer level;
	Integer portrait;
	Integer heartNum;
	Integer victoryNum;
	Integer loserNum;
	Integer runawayNum;
	Integer medalInUse;
	Integer itemInUse1;
	Integer itemInUse2;
	Integer itemInUse3;
	
	
	
	private String inUse;
	
	transient IoSession session;
	
	public OnlineUserDto() {}
	public OnlineUserDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.heartNum = user.getHeartNum();
		this.level = user.getLevel();
		this.itemInUse1 = user.getItemInUse1();
		this.itemInUse2 = user.getItemInUse2();
		this.itemInUse3 = user.getItemInUse3();
		this.loserNum = user.getLoserNum();
		this.medalInUse = user.getMedalInUse();
		this.nickname = user.getNickName();
		this.portrait = user.getPortrait();
		this.runawayNum = user.getRunawayNum();
		this.victoryNum = user.getVictoryNum();
		this.inUse = user.getInUse();
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getPortrait() {
		return portrait;
	}

	public void setPortrait(Integer portrait) {
		this.portrait = portrait;
	}

	public Integer getHeartNum() {
		return heartNum;
	}

	public void setHeartNum(Integer heartNum) {
		this.heartNum = heartNum;
	}

	public Integer getVictoryNum() {
		return victoryNum;
	}

	public void setVictoryNum(Integer victoryNum) {
		this.victoryNum = victoryNum;
	}

	public Integer getLoserNum() {
		return loserNum;
	}

	public void setLoserNum(Integer loserNum) {
		this.loserNum = loserNum;
	}

	public Integer getRunawayNum() {
		return runawayNum;
	}

	public void setRunawayNum(Integer runawayNum) {
		this.runawayNum = runawayNum;
	}

	public Integer getMedalInUse() {
		return medalInUse;
	}

	public void setMedalInUse(Integer medalInUse) {
		this.medalInUse = medalInUse;
	}

	public Integer getItemInUse1() {
		return itemInUse1;
	}

	public void setItemInUse1(Integer itemInUse1) {
		this.itemInUse1 = itemInUse1;
	}

	public Integer getItemInUse2() {
		return itemInUse2;
	}

	public void setItemInUse2(Integer itemInUse2) {
		this.itemInUse2 = itemInUse2;
	}

	public Integer getItemInUse3() {
		return itemInUse3;
	}

	public void setItemInUse3(Integer itemInUse3) {
		this.itemInUse3 = itemInUse3;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	public String getInUse() {
		return inUse;
	}
	public void setInUse(String inUse) {
		this.inUse = inUse;
	}

	
	
	
	
}
