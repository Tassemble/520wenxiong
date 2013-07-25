package com.game.core.dto;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoomDto {
	
	
	public static final String ROOM_STATUS_OPEN = "open";
	public static final String ROOM_STATUS_CLOSED = "closed";
	
	
	private static transient AtomicLong	idGenerator	= new AtomicLong(1);
	private transient Object				roomLock	= new Object();
	private Integer	maxplayersnum;
	
	private Integer	minplayersnum;

	String						id;

	transient List<OnlineUserDto>			users;
	transient AtomicInteger				cntNow		= new AtomicInteger(0);

	
	String roomStatus;
	
	
	public String getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(String roomStatus) {
		this.roomStatus = roomStatus;
	}

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
		if (cntNow.get() < maxplayersnum) {
			cntNow.addAndGet(1);
		} else {
			throw new RuntimeException("enter room failed");
		}
	}

	public void decreaseCnt() {
		synchronized (roomLock) {
			if (cntNow.get() >= 1) {
				cntNow.addAndGet(-1);
			}
			if (cntNow.get() < 0 ){
				cntNow.set(0);
			}
		}
	}


	public Integer getMaxplayersnum() {
		return maxplayersnum;
	}

	public void setMaxplayersnum(Integer maxplayersnum) {
		this.maxplayersnum = maxplayersnum;
	}

	public Integer getMinplayersnum() {
		return minplayersnum;
	}

	public void setMinplayersnum(Integer minplayersnum) {
		this.minplayersnum = minplayersnum;
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
	
	public boolean isFull() {
		return cntNow.get() == maxplayersnum;
	}
	
	public boolean isReadyToStart() {
		return cntNow.get() == minplayersnum;
	}

	
	public boolean isEmpty() {
		return cntNow.get() == 0;
	}
	
}
