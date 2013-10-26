package com.game.core.bomb.dispatcher;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.MessageSenderHelper;
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.dto.ActionNameEnum;
import com.game.core.dto.BaseActionDataDto;
import com.game.core.dto.BaseActionDataDto.FastJoinData;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnDto;
import com.game.core.dto.RoomDto;
import com.game.core.dto.RoomDto.TimeoutCallback;
import com.game.core.utils.CellLocker;
import com.google.common.collect.ImmutableMap;
import com.wenxiong.utils.WordPressUtils;


@Component
public class FastJoinAction implements BaseAction{

	@Autowired
	CellLocker<List<String>>	locker;
	
	
	@Autowired
	RoomLogic roomLogic;
	
	@Override
	public void doAction(IoSession session, BaseActionDataDto data) {
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
			//这里不需要同步，原因是在没有创建好房间的时候，其他用户是看到这个房间的
			room = new RoomDto(userNumLimit);
			room.increaseCnt();
			room.setRoomStatus(RoomDto.ROOM_STATUS_OPEN);
			List<OnlineUserDto> users = new CopyOnWriteArrayList<OnlineUserDto>();
			users.add(user);
			String id = RoomDto.generateRoomId();
			user.setRoomId(id);
			user.setStatus(OnlineUserDto.STATUS_IN_ROOM);
			room.setId(id);
			room.setUsers(users);
			room.addUserCallback(new TimeoutCallback(user.getUsername(), joinData.getTimeoutInSeconds()));
			GameMemory.room.put(id, room);
		} else {
			roomLogic.doUserJoin(room, user.getUsername());
			room.addUserCallback(new TimeoutCallback(user.getUsername(), joinData.getTimeoutInSeconds()));
		}
		
		
		//~enter room
//		Map<String, Object> extAttrs = Maps.newHashMap();
//		extAttrs.put("players", room.getUsers());
//		extAttrs.put("room", room);
//		ReturnDto returnDto = new ReturnDto(200, action, "you enter room(" + room.getId()
//				+ "), num of players: " + room.getCntNow());
//		returnDto.setExtAttrs(extAttrs);
//		session.write(WordPressUtils.toJson(returnDto));
		
		
		//~notify other players
//		ReturnDto returnDto = new ReturnDto(200, OnlineUserDto.ACTION_SYSTEM_BROADCAST, user.getUsername()
//				+ " enter room(" + room.getId() + "), num of players: " + room.getCntNow());
//		Map<String, Object> extAttrs = Maps.newHashMap();
//		extAttrs.put("newPlayer", user);
//		returnDto.setExtAttrs(extAttrs);
//		MessageSenderHelper.forwardMessageInRoom(
//				WordPressUtils.toJson(returnDto));
		if (room.isReadyToStart()) {
			ReturnDto ro = new ReturnDto(200, this.getAction(), "players can play game now, game started!");
			ro.setExtAttrs(ImmutableMap.of("players", room.getUsers(), "room", room));
			MessageSenderHelper.forwardMessage(room.getId(), WordPressUtils.toJson(ro));
		}		
		
		
	}

	@Override
	public String getAction() {
		return ActionNameEnum.FAST_JOIN.getAction();
	}
	
	
	private void checkUserStatus(OnlineUserDto user) {
		// TODO Auto-generated method stub
		if (user.getStatus() != OnlineUserDto.STATUS_ONLINE) {
			throw new RuntimeException("user status error");
		}
	}

}
