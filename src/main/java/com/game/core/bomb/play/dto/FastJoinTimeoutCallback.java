package com.game.core.bomb.play.dto;

import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.game.core.GameMemory;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.logic.RoomLogic;

public class FastJoinTimeoutCallback implements Runnable {

	final static transient Logger		LOG					= LoggerFactory.getLogger(FastJoinTimeoutCallback.class);
	
	
	int			timeoutInSeconds	= 0;
	String		userId;
	RoomLogic	roomLogic;
	
	
	

	public FastJoinTimeoutCallback(String userId, int timeoutInSeconds) {
		super();
		this.userId = userId;
		this.timeoutInSeconds = timeoutInSeconds;
		ApplicationContext cxt = (ApplicationContext) GameMemory.bizContext.get(GameMemory.CONTEXT_NAME);
		roomLogic = cxt.getBean(RoomLogic.class);
	}

	@Override
	public void run() {

		try {
			TimeUnit.SECONDS.sleep(timeoutInSeconds);
			OnlineUserDto user = GameMemory.getUserByUsername(this.userId);
			if (user == null) {
				return;
			}
			PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
			if (room == null) {
				return;
			}
			if (PlayRoomDto.ROOM_STATUS_OPEN.equals(room.getRoomStatus())) {
				roomLogic.shutdownRoom(room);
				IoSession session = GameMemory.getSessionByUsername(this.userId);
				session.write(new ReturnDto(-20, ActionNameEnum.FAST_JOIN.getAction(), "fast-join timeout"));
			} else {
			}
			// LOG.info("game is started");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
