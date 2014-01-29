package com.game.core.bomb.logic;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.JsonSessionWrapper;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.GameSessionContext;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.bomb.play.dto.PlayersOfRoomStart;
import com.game.core.exception.ExceptionConstant;
import com.game.core.exception.GamePlayException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Component
public class RoomLogic {

	@Autowired
	UserService userService;
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
			OnlineUserDto user = GameMemory.getOnlineUserById(uid);
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
		if (room == null) {
			return;
		}
		
		
		//这里是处理快速开始游戏的逻辑处理，如果期间有人退出就直接全部退出好了，重新再进行一次游戏匹配
		if (room.getRoomStatus().equals(PlayRoomDto.ROOM_STATUS_OPEN)) {//not playing
			shutdownRoom(room);
			return;
		}
		
		
		
		//准备数据
		OnlineUserDto userInGame = GameMemory.getOnlineUserById(uid);
		IoSession session = GameMemory.getSessionByUid(uid);
		//query user from db
		final User userInDB = userService.getById(uid);

		
		boolean isLastPlayer = false;
		
		//update 
		final User update2DB = new User();
		update2DB.setId(uid);
		
		synchronized (room.getRoomLock()) {
			//如果是最后一个玩家
			if (room.getReadyNumNow() == 1) {
				isLastPlayer = true;
				update2DB.setVictoryNum(userInDB.getVictoryNum() + 1);
			} else {
				update2DB.setLoserNum(userInDB.getLoserNum() + 1);
			}
			
			userInGame.setRoomId("");
			userInGame.setStatus(OnlineUserDto.STATUS_ONLINE);
			room.getUsers().remove(userInGame);
			room.decreaseReadyNum();
			if (room.isEmpty()) {
				//把房间删除吧
				GameMemory.room.remove(room.getId());
//				room.setRoomStatus(PlayRoomDto.ROOM_STATUS_OPEN);
			}
		}
		
		if (isLastPlayer) {
			//最后一人胜利 不减红心
			Map<String, Object> maps = Maps.newHashMap();
			maps.put("action", "win");
			maps.put("user", new MobileUserDto(userInGame, userInDB));
			maps.put("code", 200);
			session.write(maps);
			
			update2DB.setLevel(changeLevelWhenWin(update2DB.getVictoryNum()));
			update2DB.setGold(userInDB.getGold() + 4);
			update2DB.setBloodTime(new Date());
			update2DB.setGmtModified(new Date());
			userService.updateSelectiveById(update2DB);
		} else {
			
			Map<String, Object> maps = Maps.newHashMap();
			maps.put("action", "lose");
			maps.put("user", new MobileUserDto(userInGame, userInDB));
			maps.put("code", 200);
			session.write(maps);
			
			update2DB.setHeartNum(userInDB.getHeartNum() - 1);
			update2DB.setGold(userInDB.getGold() + 1);
			update2DB.setBloodTime(new Date());
			update2DB.setGmtModified(new Date());
			userService.updateSelectiveById(update2DB);
		}
		
		//向所有
		ReturnDto ro = new ReturnDto(200, ActionNameEnum.QUIT_GAME.getAction(), ActionNameEnum.QUIT_GAME.getAction());
		ro.setExtAttrs(ImmutableMap.of("user", new MobileUserDto(userInGame, userInDB)));
		forwardMessageToOtherClientsInRoom(session, userInGame, ro);
		
		
		//异常情况，如果对手是逃跑的话，这时候要通知另外一个人，表示他已经断线了 或者逃跑了
		if (!CollectionUtils.isEmpty(room.getUsers())) {
			if (room.getUsers().size() == 1) {//last players
				doUserQuit(room, room.getUsers().get(0).getId());
			}
		}
	}
	
	
	
	public int changeLevelWhenWin(Integer victoryNum) {
		int level = 1;
		if (victoryNum == null || victoryNum < 5) {
			level = 1;
		} else if (victoryNum < 15 && victoryNum >= 5) {
			level = 2;
		} else if (victoryNum < 35 && victoryNum >= 15) {
			level = 3;
		} else if (victoryNum < 80 && victoryNum >= 35) {
			level = 4;
		} else if (victoryNum < 150 && victoryNum >= 80) {
			level = 5;
		} else if (victoryNum < 450 && victoryNum >= 150) {
			level = 6;
		} else if (victoryNum < 1000 && victoryNum <= 450) {
			level = 7;
		} else {
			level = 8;
		}
		return level;
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
		if (CollectionUtils.isNotEmpty(users)) {
			for (OnlineUserDto u : users) {
				try {
					if (!u.getId().equals(user.getId())) {
						u.getSession().write(message);
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			}
		}
	}
	
}
