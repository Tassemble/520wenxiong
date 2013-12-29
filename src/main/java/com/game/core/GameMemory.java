package com.game.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;

import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;
import com.game.core.bomb.dto.GameSessionContext;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.play.dto.PlayRoomDto;


public class GameMemory {
	
	
	public static final String CONTEXT_NAME = "ctx";
	
	static final int MAX_PLAYERS = 5000;

	public static Map<Long, OnlineUserDto> SESSION_USERS;
	
	public static Map<String, OnlineUserDto> ONLINE_USERS;
	
	public static Map<String, Object> actionMapping = new HashMap<String, Object>();
	
	public static Map<String, PlayRoomDto> room;
	
	public static ExecutorService executor = null;
	
	public static Map<String, Object> bizContext;
	
	
	
	public static boolean hasLogin() {
		if (getUser() != null) {
			return true;
		}
		return false;
	}
	
	public static void reloadUser() {
		UserService userService = ApplicationContextHolder.get().getBean(UserService.class);
		if (getUser() == null) {
			throw new RuntimeException("reload user error due to threadlocal no user find");
		}
		User user = userService.getById(getUser().getId());
		setUser(new OnlineUserDto(user));
	}
	
	//并不能保证一个session一直在同一个线程中，因此，在返回信息的时候要清除session
	public static ThreadLocal<GameSessionContext> LOCAL_SESSION_CONTEXT = new ThreadLocal<GameSessionContext>();
	
	static {
		//用于用于超时通知
		executor = Executors.newFixedThreadPool(MAX_PLAYERS);
		//key is session id , value is user
		SESSION_USERS = new ConcurrentHashMap<Long, OnlineUserDto>();
		//key is username , value is user
		ONLINE_USERS = new ConcurrentHashMap<String, OnlineUserDto>();
		room = new ConcurrentHashMap<String, PlayRoomDto>();
		
		// for biz context
		bizContext  = new ConcurrentHashMap<String, Object>();
	}
	
	
	
	public static IoSession getCurrentSession() {
		return LOCAL_SESSION_CONTEXT.get().getSession();
	}
	
	public static void write(Object message) {
		LOCAL_SESSION_CONTEXT.get().getSession().write(message);
	}
	
	
	public static OnlineUserDto getUserByUsername(String username) {
		return ONLINE_USERS.get(username);
	}
	
	
	public static OnlineUserDto getUser() {
		return LOCAL_SESSION_CONTEXT.get().getOnlineUser();
	}
	
	public static void setUser(OnlineUserDto user) {
		LOCAL_SESSION_CONTEXT.get().setOnlineUser(user);
	}
	
	public static OnlineUserDto getOnlineUserBySessionId(Long id) {
		return SESSION_USERS.get(id);
	}
	
	public static PlayRoomDto getRoomByRoomId(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		return room.get(id);
	}
	
	public static void addNewUserToOnlineUserList() {
		
	}
	
	public static void put(Long key, OnlineUserDto value) {
		SESSION_USERS.put(key, value);
	}
	
	
	public static Object get(Long key) {
		return SESSION_USERS.get(key);
	}
	
	
	public static void removeSessionUserByKey(Long key) {
		SESSION_USERS.remove(key);
	}


	public static Map<Long, OnlineUserDto> getMap() {
		return SESSION_USERS;
	}


	public static Map<String, PlayRoomDto> getRoom() {
		return room;
	}
	
	
	public static IoSession getSessionByUsername(String username) {
		OnlineUserDto dto = ONLINE_USERS.get(username);
		if (dto != null) {
			return dto.getSession();
		}
		return null;
	}

	
	
}
