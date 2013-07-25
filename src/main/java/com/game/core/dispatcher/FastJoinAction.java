package com.game.core.dispatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.MessageSenderHelper;
import com.game.core.dto.JsonDto.BaseJsonData;
import com.game.core.dto.JsonDto.FastJoinData;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnDto;
import com.game.core.dto.RoomDto;
import com.game.core.utils.CellLocker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wenxiong.utils.WordPressUtils;


@Component
public class FastJoinAction implements BaseAction{

	@Autowired
	CellLocker<List<String>>	locker;
	
	@Override
	public void doAction(IoSession session, BaseJsonData data) {
		String action = this.getAction();
		
		
		// check user status
		OnlineUserDto user = GameMemory.sessionUsers.get(session.getId());
		checkUserStatus(user);
		FastJoinData joinData = (FastJoinData) data;
		int userNumLimit = joinData.getPlayersNum();

		RoomDto room = null;
		for (Entry<String, RoomDto> entry : GameMemory.room.entrySet()) {
			if (entry.getValue().getCntNow() < entry.getValue().getPlayersNum()
					&& entry.getValue().getPlayersNum() == userNumLimit
					&& RoomDto.ROOM_STATUS_OPEN == entry.getValue().getRoomStatus()) {
				room = entry.getValue();
				break;
			}
		}
		if (room == null) {// create new room
			room = new RoomDto();
			room.setPlayersNum(userNumLimit);
			room.increaseCnt();
			room.setRoomStatus(RoomDto.ROOM_STATUS_OPEN);
			List<OnlineUserDto> users = Lists.newArrayList();
			users.add(user);
			String id = RoomDto.getRoomId();
			user.setRoomId(id);
			user.setStatus(OnlineUserDto.STATUS_IN_ROOM);
			room.setId(id);
			room.setUsers(users);
			GameMemory.room.put(id, room);
		} else {
			// LOCK HERE
			List<String> key = Arrays.asList(String.valueOf(room.getId()));
			try {
				locker.lock("", key);
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
			} finally {
				locker.unLock("", key);
			}
		}
		Map<String, Object> extAttrs = Maps.newHashMap();
		extAttrs.put("players", room.getUsers());
		extAttrs.put("room", room);
		ReturnDto returnDto = new ReturnDto(200, action, "you enter room(" + room.getId()
				+ "), num of players: " + room.getCntNow());
		returnDto.setExtAttrs(extAttrs);
		
		session.write(WordPressUtils.toJson(returnDto));
		
		returnDto = new ReturnDto(200, OnlineUserDto.ACTION_SYSTEM_BROADCAST, user.getUsername()
				+ " enter room(" + room.getId() + "), num of players: " + room.getCntNow());
		extAttrs = Maps.newHashMap();
		extAttrs.put("newPlayer", user);
		returnDto.setExtAttrs(extAttrs);
		MessageSenderHelper.forwardMessageInRoom(
				WordPressUtils.toJson(returnDto));
		if (room.isReadyToStart()) {
			MessageSenderHelper.forwardMessage(room.getId(), WordPressUtils.toJson(new ReturnDto(200,
					OnlineUserDto.ACTION_SYSTEM_BROADCAST, "players can play game now, game started!")));

		}		
		
		
	}

	@Override
	public String getAction() {
		return FAST_JOIN;
	}
	
	
	private void checkUserStatus(OnlineUserDto user) {
		// TODO Auto-generated method stub
		if (user.getStatus() != OnlineUserDto.STATUS_ONLINE) {
			throw new RuntimeException("user status error");
		}
	}

}
