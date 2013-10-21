package com.game.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;

import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.RoomDto;
import com.wenxiong.utils.WordPressUtils;


public class GameMemory {
	
	
	public static final String CONTEXT_NAME = "ctx";
	
	static final int MAX_PLAYERS = 5000;

	public static Map<Long, OnlineUserDto> sessionUsers;
	
	public static Map<String, OnlineUserDto> onlineUsers;
	
	public static Map<String, Object> actionMapping = new HashMap<String, Object>();
	
	public static Map<String, RoomDto> room;
	
	public static ExecutorService executor = null;
	
	public static Map<String, Object> bizContext;
	
	
	
	
	
	//并不能保证一个session一直在同一个线程中，因此，在返回信息的时候要清除session
	static ThreadLocal<IoSession> LOCAL_SESSION = new ThreadLocal<IoSession>();
	
	static ThreadLocal<OnlineUserDto> LOCAL_USER = new ThreadLocal<OnlineUserDto>();
	
	static {
		//用于用于超时通知
		executor = Executors.newFixedThreadPool(MAX_PLAYERS);
		//key is session id , value is user
		sessionUsers = new ConcurrentHashMap<Long, OnlineUserDto>();
		//key is username , value is user
		onlineUsers = new ConcurrentHashMap<String, OnlineUserDto>();
		room = new ConcurrentHashMap<String, RoomDto>();
		
		// for biz context
		bizContext  = new ConcurrentHashMap<String, Object>();
	}
	
	public static IoSession getCurrentSession() {
		return LOCAL_SESSION.get();
	}
	
	public static void write(Object message) {
		String strMessage = WordPressUtils.toJson(message);
		LOCAL_SESSION.get().write(strMessage);
	}
	
	
	public static OnlineUserDto getUserByUsername(String username) {
		return onlineUsers.get(username);
	}
	
	
	public static OnlineUserDto getUser() {
		return LOCAL_USER.get();
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
