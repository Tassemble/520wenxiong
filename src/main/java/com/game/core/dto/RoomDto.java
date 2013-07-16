package com.game.core.dto;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoomDto {
	private static AtomicLong	idGenerator	= new AtomicLong(1);
	private Object				roomLock	= new Object();
	int							userNumLimit;

	String						id;

	List<OnlineUserDto>			users;
	AtomicInteger				cntNow		= new AtomicInteger(0);

	public List<OnlineUserDto> getUsers() {
		return users;
	}

	public int getCntNow() {
		return cntNow.get();
	}

	public void setUsers(List<OnlineUserDto> users) {
		this.users = users;
	}

	public void increaseCnt() {
		synchronized (roomLock) {
			if (cntNow.get() < userNumLimit) {
				cntNow.addAndGet(1);
			} else {
				throw new RuntimeException("enter room failed");
			}
		}
	}

	public void decreaseCnt() {
		synchronized (roomLock) {
			if (cntNow.get() >= 1) {
				cntNow.addAndGet(-1);
			}
		}
	}

	public int getUserNumLimit() {
		return userNumLimit;
	}

	public void setUserNumLimit(int userNumLimit) {
		this.userNumLimit = userNumLimit;
	}

	public static String getRoomId() {
		return String.valueOf(idGenerator.getAndAdd(1L));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
