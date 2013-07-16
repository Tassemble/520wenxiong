package com.game.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.RoomDto;


public class GameMemory {

	static Map<Long, OnlineUserDto> userContainer;
	
	static Map<String, RoomDto> room;
	
	static {
		userContainer = new ConcurrentHashMap<Long, OnlineUserDto>();
		room = new ConcurrentHashMap<String, RoomDto>();
	}
	
	
	public static void put(Long key, OnlineUserDto value) {
		userContainer.put(key, value);
	}
	
	
	public static Object get(Long key) {
		return userContainer.get(key);
	}
	
	
	public static void remove(String key) {
		userContainer.remove(key);
	}


	public static Map<Long, OnlineUserDto> getMap() {
		return userContainer;
	}


	public static Map<String, RoomDto> getRoom() {
		return room;
	}

	
	
}
