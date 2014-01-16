package com.game.core.bomb.logic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.Dao.UserDao;
import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.core.GameMemory;
import com.game.core.JsonSessionWrapper;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.GameSessionContext;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.ExceptionConstant;
import com.game.core.exception.GamePlayException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Component
public class RoomLogic {

	
	@Autowired
	UserDao userDao;
	
	
	private static final Logger	LOG		= LoggerFactory.getLogger(RoomLogic.class);
	
	
	

	public static void destroyRoom(PlayRoomDto room) {
		synchronized (room.getRoomLock()) {
			if (!CollectionUtils.isEmpty(room.getUsers())) {
				for (OnlineUserDto user : room.getUsers()) {
					user.setStatus(OnlineUserDto.STATUS_ONLINE);
				};
			}
			
			room.setUsers(null);
			GameMemory.getRoom().remove(room.getId());
			room = null;
		}
	}

	public void doUserJoin(PlayRoomDto room, String username, boolean withReady) {
		
	}
	
	
	
	public void doUserJoin(PlayRoomDto room, Long uid) {
		synchronized (room.getRoomLock()) {
			OnlineUserDto user = GameMemory.getUserById(uid);
			room.increaseReadyNum();
			room.getUsers().add(user);
			user.setRoomId(room.getId());
			user.setStatus(OnlineUserDto.STATUS_IN_ROOM);
			if (room.isReadyToStart()) {
				// start game
				//change room status
				startGame(room);
			}
		}
	}
	
	
	
	public void startGame(PlayRoomDto room) {
		if (room.isReadyToStart()) {
			if (PlayRoomDto.ROOM_STATUS_OPEN.equals(room.getRoomStatus())) {
				room.startGame();
			} else {
				//is not open may be playing
				throw new GamePlayException(ExceptionConstant.GAME_START_EXCEPTION, "game has start but still want to startGame");
			}
		}
	}
	
	
	public static void forwardMessageInRoom(String roomId, Object message) {
		if (roomId == null) {
			return;
		}

		PlayRoomDto room = GameMemory.getRoom().get(roomId);
		if (room == null) {
			return;
		}
		List<OnlineUserDto> users = room.getUsers();
		
		for (OnlineUserDto u : users) {
			u.getSession().write(message);
		}
	}
	
	
	public void shutdownRoom(PlayRoomDto room) {
		destroyRoom(room);
	}
	
	public void doUserQuit(PlayRoomDto room, Long uid) throws Exception {
		//这里是处理快速开始游戏的逻辑处理，如果期间有人退出就直接全部退出好了，重新再进行一次游戏匹配
		if (room.getRoomStatus().equals(PlayRoomDto.ROOM_STATUS_OPEN)) {//not playing
			shutdownRoom(room);
			return;
		}
		
		OnlineUserDto user = GameMemory.getUserById(uid);
		IoSession session = GameMemory.getSessionById(uid);
		
		ReturnDto ro = new ReturnDto(200, ActionNameEnum.QUIT_GAME.getAction(), ActionNameEnum.QUIT_GAME.getAction());
		ro.setExtAttrs(ImmutableMap.of("user", new MobileUserDto(user)));

		forwardMessageToOtherClientsInRoom(session, user, ro);
		
		boolean isLastPlayer = false;
		synchronized (room.getRoomLock()) {
			//如果是最后一个玩家
			if (room.getReadyNumNow() == 1) {
				isLastPlayer = true;
				user.setVictoryNum(user.getVictoryNum() + 1);
			} else {
				user.setLoserNum(user.getLoserNum() + 1);
			}
			user.setHeartNum(user.getHeartNum() - 1);
			user.setRoomId("");
			user.setStatus(OnlineUserDto.STATUS_ONLINE);
			room.getUsers().remove(user);
			room.decreaseReadyNum();
			if (room.isEmpty()) {
				//把房间删除吧
				GameMemory.room.remove(room.getId());
//				room.setRoomStatus(PlayRoomDto.ROOM_STATUS_OPEN);
			}
		}
		
		if (isLastPlayer) {
			Map<String, Object> maps = Maps.newHashMap();
			maps.put("action", "win");
			maps.put("user", new MobileUserDto(user));
			maps.put("code", 200);
			session.write(maps);
			
			changeLevelWhenWin(user);
			User update = new User();
			update.setId(user.getId());
			update.setLevel(user.getLevel());
			update.setHeartNum(user.getHeartNum());
			update.setVictoryNum(user.getVictoryNum());
			update.setBloodTime(new Date());
			update.setGmtModified(new Date());
			userDao.updateSelectiveById(update);
		} else {
			Map<String, Object> maps = Maps.newHashMap();
			maps.put("action", "lose");
			maps.put("user", new MobileUserDto(user));
			maps.put("code", 200);
			session.write(maps);
			
			User update = new User();
			update.setId(user.getId());
			update.setBloodTime(new Date());
			update.setHeartNum(user.getHeartNum());
			update.setGmtModified(new Date());
			update.setLoserNum(user.getLoserNum());
			update.setRunawayNum(user.getRunawayNum());
			userDao.updateSelectiveById(update);
		}
		
		
		if (!CollectionUtils.isEmpty(room.getUsers())) {
			if (room.getUsers().size() == 1) {//last players
				doUserQuit(room, room.getUsers().get(0).getId());
			}
		}
		
		GameMemory.reloadUser();
	}
	
	
	
	public void changeLevelWhenWin(OnlineUserDto user) {
		if (user.getVictoryNum() == null || user.getVictoryNum() < 5) {
			user.setLevel(1);
		} else if (user.getVictoryNum() < 15 && user.getVictoryNum() >= 5) {
			user.setLevel(2);
		} else if (user.getVictoryNum() < 35 && user.getVictoryNum() >= 15) {
			user.setLevel(3);
		} else if (user.getVictoryNum() < 80 && user.getVictoryNum() >= 35) {
			user.setLevel(4);
		} else if (user.getVictoryNum() < 150 && user.getVictoryNum() >= 80) {
			user.setLevel(5);
		} else if (user.getVictoryNum() < 450 && user.getVictoryNum() >= 150) {
			user.setLevel(6);
		} else if (user.getVictoryNum() < 1000 && user.getVictoryNum() <= 450) {
			user.setLevel(7);
		} else {
			user.setLevel(8);
		}
	}
	
	public int getLevel(Integer winNum) {
		int winNumCopy = winNum;
		for (int i = 0; i < winNum; i++) {
			winNumCopy -= i;
			if (winNumCopy < 0) {
				return i - 1;
			}
		}
		return 0;
	}
	
	/**
	 * @param session
	 * @param message
	 * @param user
	 */
	public static void forwardMessageToOtherClientsInRoom(Object message) {
		GameSessionContext context = GameMemory.LOCAL_SESSION_CONTEXT.get();
		IoSession session = context.getSession();
		OnlineUserDto user = GameMemory.getUser();
		
		LOG.info("forward message to other clients");
		// forward to same room clients
		if (user.getRoomId() == null) {
			session.write(new ReturnDto(-1,
					"current user has not joined room, discard messages!!"));
			return;
		}

		PlayRoomDto room = GameMemory.getRoom().get(user.getRoomId());
		if (room == null) {
			session.write(new ReturnDto(-1,
					"current user has not joined room, discard messages!!"));
			return;
		}
		List<OnlineUserDto> users = room.getUsers();
		for (OnlineUserDto u : users) {
			if (!u.getId().equals(user.getId())) {
				if (u.getSession() instanceof JsonSessionWrapper) {
					JsonSessionWrapper sessionWrapper = (JsonSessionWrapper)u.getSession();
					sessionWrapper.getSession().write(message);
				} else {
					u.getSession().write(message);
				}
			}
		}
	}
	
	
	public static void forwardMessageToOtherClientsInRoom(List<OnlineUserDto> users, Object message) {
		OnlineUserDto user = GameMemory.getUser();
		
		for (OnlineUserDto u : users) {
			if (!u.getId().equals(user.getId())) {
				if (u.getSession().isConnected()) {
					u.getSession().write(message);
				}
			}
		}
	}
	
	
	
	
	public static void forwardMessageToOtherClientsInRoom(IoSession session, OnlineUserDto user, Object message) {
		LOG.info("forward message to other clients");
		// forward to same room clients
		if (user.getRoomId() == null) {
			session.write(new ReturnDto(-1,
					"current user has not joined room, discard messages!!"));
			return;
		}

		PlayRoomDto room = GameMemory.getRoom().get(user.getRoomId());
		if (room == null) {
			session.write(new ReturnDto(-1,
					"current user has not joined room, discard messages!!"));
			return;
		}
		List<OnlineUserDto> users = room.getUsers();
		for (OnlineUserDto u : users) {
			if (!u.getId().equals(user.getId())) {
				u.getSession().write(message);
			}
		}
	}
	
}
