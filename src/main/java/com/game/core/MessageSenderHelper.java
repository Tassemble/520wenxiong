package com.game.core;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnDto;
import com.game.core.dto.RoomDto;
import com.wenxiong.utils.WordPressUtils;

public class MessageSenderHelper {

	private static final Logger	LOG		= LoggerFactory.getLogger(MessageSenderHelper.class);
	public static void forwardMessage(String roomId, String json) {
		if (roomId == null) {
			return;
		}

		RoomDto room = GameMemory.getRoom().get(roomId);
		if (room == null) {
			return;
		}
		List<OnlineUserDto> users = room.getUsers();
		for (OnlineUserDto u : users) {
			u.getSession().write(json);
		}
	}
	
	
	public static void forwardMessage(IoSession session, Object message) {
		
		session.write(message);
	}
	
	/**
	 * @param session
	 * @param message
	 * @param user
	 */
	public static void forwardMessageInRoom(Object message) {
		IoSession session = GameMemory.LOCAL_SESSION.get();
		OnlineUserDto user = GameMemory.LOCAL_USER.get();
		
		LOG.info("forward message to other clients");
		// forward to same room clients
		if (user.getRoomId() == null) {
			session.write(WordPressUtils.toJson(new ReturnDto(-1,
					"current user has not joined room, discard messages!!")));
			return;
		}

		RoomDto room = GameMemory.getRoom().get(user.getRoomId());
		if (room == null) {
			session.write(WordPressUtils.toJson(new ReturnDto(-1,
					"current user has not joined room, discard messages!!")));
			return;
		}
		List<OnlineUserDto> users = room.getUsers();
		for (OnlineUserDto u : users) {
			if (!u.getUsername().equals(user.getUsername())) {
				u.getSession().write(message);
			}
		}
	}
}
