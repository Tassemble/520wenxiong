package com.game.core.bomb.logic;

import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.Dao.UserDao;
import com.game.bomb.domain.User;
import com.game.core.GameMemory;
import com.game.core.MessageSenderHelper;
import com.game.core.dto.ActionNameEnum;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnDto;
import com.game.core.dto.RoomDto;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Component
public class RoomLogic {

	
	@Autowired
	UserDao userDao;
	
	
	public void doUserJoin(RoomDto room, String username) {
		synchronized (room.getRoomLock()) {
			OnlineUserDto user = GameMemory.getUserByUsername(username);
			room.increaseCnt();
			room.getUsers().add(user);
			user.setRoomId(room.getId());
			user.setStatus(OnlineUserDto.STATUS_IN_ROOM);
			if (room.isReadyToStart()) {
				// start game
				room.setRoomStatus(RoomDto.ROOM_STATUS_CLOSED);
				for (OnlineUserDto userDto : room.getUsers()) {
					userDto.setStatus(OnlineUserDto.STATUS_PLAYING);
				}
			}
		}
	}
	
	public void doUserQuit(RoomDto room, String username) {
		OnlineUserDto user = GameMemory.getUserByUsername(username);
		
		IoSession session = GameMemory.getSessionByUsername(username);
		ReturnDto ro = new ReturnDto(200, ActionNameEnum.QUIT_GAME.getAction(), ActionNameEnum.QUIT_GAME.getAction());
		ro.setExtAttrs(ImmutableMap.of("user", user));

		MessageSenderHelper.forwardMessageToOtherClientsInRoom(session, user, ro);
		
		boolean isLastPlayer = false;
		synchronized (room.getRoomLock()) {
			//如果是最后一个玩家
			if (room.getCntNow() == 1) {
				isLastPlayer = true;
			}
			user.setRoomId("");
			user.setStatus(OnlineUserDto.STATUS_ONLINE);
			user.setVictoryNum(user.getVictoryNum() + 1);
			
			user.setLevel(getLevel(user.getVictoryNum()));
			room.getUsers().remove(user);
			room.decreaseCnt();
			if (room.isEmpty()) {
				room.setRoomStatus(RoomDto.ROOM_STATUS_OPEN);
			}
		}
		
		if (isLastPlayer) {
			Map<String, Object> maps = Maps.newHashMap();
			maps.put("action", "win");
			maps.put("user", user);
			maps.put("code", 200);
			session.write(maps);
			
			User update = new User();
			update.setId(user.getId());
			update.setLevel(user.getLevel());
			update.setVictoryNum(user.getVictoryNum() + 1);
			userDao.updateSelectiveById(update);
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
	
}
