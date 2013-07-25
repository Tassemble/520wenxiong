package com.game.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ConcurrentHashSet;

import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.RoomDto;


public class GameMemory {

	public static Map<Long, OnlineUserDto> sessionUsers;
	
	public static Map<String, OnlineUserDto> onlineUsers;
	
	public static Map<String, RoomDto> room;
	
	static ThreadLocal<OnlineUserDto> LocalUser = new ThreadLocal<OnlineUserDto>();
	
	static {
		//key is session id , value is user
		sessionUsers = new ConcurrentHashMap<Long, OnlineUserDto>();
		//key is username , value is user
		onlineUsers = new ConcurrentHashMap<String, OnlineUserDto>();
		room = new ConcurrentHashMap<String, RoomDto>();
	}
	
	
	
	public static OnlineUserDto getOnlineUser() {
		return LocalUser.get();
	}
	
	public static OnlineUserDto getOnlineUserBySessionId(Long id) {
		return sessionUsers.get(id);
	}
	
	public static RoomDto getRoomByRoomId(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		return room.get(id);
	}
	
	public static void addNewUserToOnlineUserList() {
		
	}
	
	public static void put(Long key, OnlineUserDto value) {
		sessionUsers.put(key, value);
	}
	
	
	public static Object get(Long key) {
		return sessionUsers.get(key);
	}
	
	
	public static void removeSessionUserByKey(Long key) {
		sessionUsers.remove(key);
	}


	public static Map<Long, OnlineUserDto> getMap() {
		return sessionUsers;
	}


	public static Map<String, RoomDto> getRoom() {
		return room;
	}
	
	
	public static IoSession getSessionByUsername(String username) {
		OnlineUserDto dto = onlineUsers.get(username);
		if (dto != null) {
			return dto.getSession();
		}
		return null;
	}

	
	
}
