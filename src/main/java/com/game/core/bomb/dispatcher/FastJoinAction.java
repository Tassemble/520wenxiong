package com.game.core.bomb.dispatcher;

import java.util.List;
import java.util.Map.Entry;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.mobile.dto.MobRoomDto;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.core.GameMemory;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.BaseActionDataDto;
import com.game.core.bomb.dto.BaseActionDataDto.FastJoinData;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.bomb.play.dto.FastJoinTimeoutCallback;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.BombException;
import com.game.core.exception.ExceptionConstant;
import com.game.core.exception.GamePlayException;
import com.game.core.utils.CellLocker;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Component
public class FastJoinAction implements BaseAction {

	@Autowired
	CellLocker<List<String>>	locker;

	@Autowired
	RoomLogic					roomLogic;

	private static Logger		LOG	= LoggerFactory.getLogger(FastJoinAction.class);

	@Override
	public void doAction(IoSession session, BaseActionDataDto data) throws Exception {
		// check user status
		OnlineUserDto user = GameMemory.sessionUsers.get(session.getId());
		checkUserStatus(user);
		FastJoinData joinData = (FastJoinData) data;
		int userNumLimit = joinData.getPlayersNum();

		PlayRoomDto room = null;
		for (Entry<String, PlayRoomDto> entry : GameMemory.room.entrySet()) {
			if (isMatchRoom(userNumLimit, entry.getValue())) {
				room = entry.getValue();
				break;
			}
		}

		try {
			if (room == null) {// create new room
				// 这里不需要同步，原因是在没有创建好房间的时候，其他用户是看到这个房间的
				room = new PlayRoomDto(userNumLimit, user, joinData);
				GameMemory.room.put(room.getId(), room);
				LOG.info("user[" + user.getUsername() + "] create room, rid:" + room.getId());
			} else {
				roomLogic.doUserJoin(room, user.getUsername());
				room.addUserCallback(new FastJoinTimeoutCallback(user.getUsername(), joinData
						.getTimeoutInSeconds()));
				
				LOG.info("user[" + user.getUsername() + "] join room, rid:" + room.getId());

			}

			if (room.isReadyToStart()) {

				// online users
				List<MobileUserDto> players = Lists.newArrayList();
				for (OnlineUserDto oUser : room.getUsers()) {
					MobileUserDto mUser = new MobileUserDto(oUser);
					players.add(mUser);
				}

				ReturnDto ro = new ReturnDto(200, this.getAction(), "players can play game now, game started!");
				ro.setExtAttrs(ImmutableMap.of("players", players, "room", new MobRoomDto(room)));
				RoomLogic.forwardMessageInRoom(room.getId(), ro);
			}
		} catch (Exception e) {
			//this should not happen
			LOG.error(e.getMessage(), e);
			if (room != null) {
				RoomLogic.destroyRoom(room);
			}

			throw new GamePlayException(ExceptionConstant.JOIN_ROOM_FAILED, "create or join room failed. cause:"
					+ e.getMessage());
		}

	}

	private boolean isMatchRoom(int userNumLimit, PlayRoomDto room) {
		return room.getReadyNumNow() < room.getRoomNumLimit() && room.getRoomNumLimit() == userNumLimit
				&& PlayRoomDto.ROOM_STATUS_OPEN == room.getRoomStatus();
	}

	@Override
	public String getAction() {
		return ActionNameEnum.FAST_JOIN.getAction();
	}

	private void checkUserStatus(OnlineUserDto user) {
		// TODO Auto-generated method stub
		if (user.getStatus() != OnlineUserDto.STATUS_ONLINE) {
			throw new BombException(-100, "user status error");
		}
	}

}
