package com.game.core.dispatcher;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.dto.ActionNameEnum;
import com.game.core.dto.BaseActionDataDto;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.RoomDto;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.NoAuthenticationException;
import com.game.core.logic.RoomLogic;



@Component
public class QuitGameAction implements BaseAction{

	@Autowired
	RoomLogic roomLogic;
	
	@Override
	public void doAction(IoSession session, BaseActionDataDto baseData) {
		validateUserStatus(baseData);
		
		OnlineUserDto user = GameMemory.getUser();
		RoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		
		roomLogic.doUserQuit(room, user.getUsername());
	}
	
	
	private void validateUserStatus(BaseActionDataDto baseData) {
		OnlineUserDto user = GameMemory.getUser();
		
		if (user == null) {
			throw new NoAuthenticationException(baseData.getAction());
		}
		RoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		
		if (room == null) {
			throw new ActionFailedException(baseData.getAction());
		}
		
		 if (!(OnlineUserDto.STATUS_PLAYING.equals(user.getStatus()) || OnlineUserDto.STATUS_IN_ROOM.equals(user.getStatus()))) {
			 throw new ActionFailedException(baseData.getAction());
		 }
	}

	@Override
	public String getAction() {
		return ActionNameEnum.QUIT_GAME.getAction();
	}

}
