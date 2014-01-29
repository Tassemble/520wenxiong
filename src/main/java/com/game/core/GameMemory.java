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
import com.game.core.exception.GamePlayException;


public class GameMemory {
	
	public static final String CONTEXT_NAME = "ctx";
	
	static final int MAX_PLAYERS = 5000;

	public static Map<String, Object> ACTION_MAPPING = new HashMap<String, Object>();
	
	public static Map<String, PlayRoomDto> room;
	
	public static ExecutorService executor = null;
	
	public static Map<String, Object> bizContext;
	
	
	//定义为会话题有效 key is session id , value is user
	private static Map<Long, OnlineUserDto> SESSION_USERS;
	
	
	//定义为在线的游戏玩家 通过uid来查找其他的用户  目前一个session 对应一个用户
	private static Map<Long, OnlineUserDto> ONLINE_USERS;
	
	//使用类似request-response的方式  
	public static ThreadLocal<GameSessionContext> LOCAL_SESSION_CONTEXT = new ThreadLocal<GameSessionContext>();
	
	
	
	public static boolean hasLogin() {
		if (getUser() != null) {
			return true;
		}
		return false;
	}
	static {
		//用于用于超时通知
		executor = Executors.newFixedThreadPool(MAX_PLAYERS);
		
		//key is session id , value is user
		SESSION_USERS = new ConcurrentHashMap<Long, OnlineUserDto>();
		//key is uid , value is user
		ONLINE_USERS = new ConcurrentHashMap<Long, OnlineUserDto>();
		
		room = new ConcurrentHashMap<String, PlayRoomDto>();
		
		// for biz context
		bizContext  = new ConcurrentHashMap<String, Object>();
	}
	
	public static void addSessionUser(Long sessionId, OnlineUserDto user) {
		SESSION_USERS.put(sessionId, user);
	}
	
	
	public static void addToOnlineUserList(OnlineUserDto user) {
		ONLINE_USERS.put(user.getId(), user);
	}
	
	public static void removeOnlineUserByUid(Long uid) {
		ONLINE_USERS.remove(uid);
	}
	
	public static IoSession getCurrentSession() {
		return LOCAL_SESSION_CONTEXT.get().getSession();
	}
	
	public static void write(Object message) {
		LOCAL_SESSION_CONTEXT.get().getSession().write(message);
	}
	
	
	public static boolean isUserOnline(Long uid) {
		return ONLINE_USERS.containsKey(uid);
	}
	
	public static OnlineUserDto getOnlineUserById(Long uid) {
		return ONLINE_USERS.get(uid);
	}
	
	
	public static OnlineUserDto getUser() {
		GameSessionContext gsession = LOCAL_SESSION_CONTEXT.get();
		if (gsession == null) {
			throw new GamePlayException(-1, "user not online");
		}
		return gsession.getOnlineUser();
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
	
	
	public static void put(Long key, OnlineUserDto value) {
		SESSION_USERS.put(key, value);
	}
	
	
	public static Object get(Long key) {
		return SESSION_USERS.get(key);
	}
	
	
	public static void removeSessionUserBySessionId(Long sessionId) {
		SESSION_USERS.remove(sessionId);
	}


	public static Map<Long, OnlineUserDto> getMap() {
		return SESSION_USERS;
	}


	public static Map<String, PlayRoomDto> getRoom() {
		return room;
	}
	
	
	public static IoSession getSessionByUid(Long uid) {
		OnlineUserDto dto = ONLINE_USERS.get(uid);
		if (dto != null) {
			return dto.getSession();
		}
		return null;
	}

	
	
}
