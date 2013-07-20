package com.game.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.core.dto.JsonDto;
import com.game.core.dto.JsonDto.BaseJsonData;
import com.game.core.dto.JsonDto.LoginData;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnDto;
import com.game.core.dto.RoomDto;
import com.game.core.utils.CellLocker;
import com.google.common.collect.Lists;
import com.wenxiong.utils.WordPressUtils;

/**
 * @author CHQ
 * @since 20130713
 */
public class GameProtocolHandler implements IoHandler {

	private static final Logger	LOG		= LoggerFactory.getLogger(GameProtocolHandler.class);

	CellLocker<List<String>>	locker	= new CellLocker<List<String>>(65536);

	@Override
	public void sessionCreated(IoSession session) throws Exception {

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
		//
		//locker.lock("", key);
		OnlineUserDto user = GameMemory.sessionUsers.get(session.getId());
		if (user == null) {
			return;
		}
		RoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		if (room == null) {
			return;
		}
		List<String> key =Arrays.asList(String.valueOf(room.getId()));
		try {
			locker.lock("", key);
			room.getUsers().remove(user);
			room.decreaseCnt();
			
			if (room.isEmpty()) {
				room.setRoomStatus(RoomDto.ROOM_STATUS_OPEN);
			} else {
				forwardMessage(
						session,
						WordPressUtils.toJson(new ReturnDto(200, OnlineUserDto.ACTION_SYSTEM_BROADCAST, user.getUsername()
								+ " quit game (" + room.getId() + "), num of players: " + room.getCntNow())), user);
			}
		} finally {
			locker.unLock("", key);
		}
		GameMemory.onlineUsers.remove(user.getUsername());
		GameMemory.removeSessionUserByKey(session.getId());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus paramIdleStatus) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable paramThrowable) throws Exception {
		if (paramThrowable instanceof NotImplementedException) {
			session.write(WordPressUtils.toJson(new ReturnDto(-5, "this function has not implemented")));
			return;
		}

		session.write(WordPressUtils.toJson(new ReturnDto(-100, "message format is error")));
		return;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		if (LOG.isDebugEnabled()) {
			LOG.debug("receive message from session:" + session.getId() + ", message:" + message.toString());
		}
		// 特殊输出，如果是单纯字节的话========================start
		JSONObject json = null;
		String action = null;
		try {
			json = JSONObject.fromObject(message);
			action = json.getString("action");
		} catch (Exception e) {
			LOG.warn("parse json exeception");
		}

		OnlineUserDto user = GameMemory.sessionUsers.get(session.getId());
		if (user == null) {

			// is login?
			if (StringUtils.isBlank(action) || !action.equals(OnlineUserDto.ACTION_LOGIN)) {
				session.write(WordPressUtils.toJson(new ReturnDto(403, action, "no authentication")));
				return;
			}

			LoginData loginData = (LoginData) WordPressUtils.getFromJson(message.toString(),
					JsonDto.getClassByAction(action));

			if (isValid(loginData)) {// TODO 实现验证用户名和密码

				OnlineUserDto dto = new OnlineUserDto();
				dto.setSession(session);
				dto.setStatus(OnlineUserDto.STATUS_ONLINE);
				dto.setUsername(loginData.getUsername());

				if (GameMemory.onlineUsers.containsValue(dto)) {
					OnlineUserDto oldUser = GameMemory.onlineUsers.get(dto.getUsername());
					if (session.getId() != oldUser.getSession().getId()) {
						session.write(WordPressUtils.toJson(new ReturnDto(
								ReturnDto.ALREADY_LOGON_CODE_WITH_OTHER_REMOTE_CLIENT, action,
								"you have already logon in remote clinet")));
					} else {
						session.write(WordPressUtils.toJson(new ReturnDto(ReturnDto.ALREADY_LOGON_CODE, action,
								"you have already logon")));
					}
					return;
				}
				LOG.info("validate ok for username:" + loginData.getUsername());
				GameMemory.onlineUsers.put(loginData.getUsername(), dto);
				GameMemory.sessionUsers.put(session.getId(), dto);

				session.write(WordPressUtils.toJson(new ReturnDto(200, action, "logon successfully")));
				return;
			}

			session.write(WordPressUtils.toJson(new ReturnDto(-1, action, "logon failed")));
			return;

		}

		BaseJsonData data = null;
		if (StringUtils.isBlank(action)) {
			// 特殊处理
			forwardMessage(session, message, user);
			return;
		} else {

			data = (BaseJsonData) WordPressUtils.getFromJson(message.toString(), JsonDto.getClassByAction(action));
		}

		// 特殊输出，如果是单纯字节的话========================end

		// 正常逻辑
		validateAction(action);

		if (OnlineUserDto.ACTION_LOGIN.equals(action)) {
			session.write(WordPressUtils.toJson(new ReturnDto(200, action, "you have already logon")));
			return;
		}

		if (OnlineUserDto.ACTION_FORWARD.equals(action)) {
			forwardMessage(session, message, user);
			return;
		}

		if (OnlineUserDto.ACTION_FAST_JOIN.equals(action)) {
			// check user status
			//
			checkUserStatus(user);
			int userNumLimit = json.getInt("userNumLimit");

			RoomDto room = null;
			for (Entry<String, RoomDto> entry : GameMemory.room.entrySet()) {
				if (entry.getValue().getCntNow() < entry.getValue().getUserNumLimit()
						&& entry.getValue().getUserNumLimit() == userNumLimit
						&& RoomDto.ROOM_STATUS_OPEN == entry.getValue().getRoomStatus()) {
					room = entry.getValue();
					break;
				}
			}
			if (room == null) {// create new room
				room = new RoomDto();
				room.setUserNumLimit(json.getInt("userNumLimit"));
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
					
					if (room.isFull()) {
						//start game
						room.setRoomStatus(RoomDto.ROOM_STATUS_CLOSED);
						for (OnlineUserDto userDto : room.getUsers()) {
							userDto.setStatus(OnlineUserDto.STATUS_PLAYING);
						}
					}
				} finally {
					locker.unLock("", key);
				}
			}
			session.write(WordPressUtils.toJson(new ReturnDto(200, action, "enter room(" + room.getId()
					+ "), num of players: " + room.getCntNow())));
			forwardMessage(
					session,
					WordPressUtils.toJson(new ReturnDto(200, OnlineUserDto.ACTION_SYSTEM_BROADCAST, user.getUsername()
							+ " enter room(" + room.getId() + "), num of players: " + room.getCntNow())), user);
			return;
		}

		throw new NotImplementedException();
	}

	private void checkUserStatus(OnlineUserDto user) {
		// TODO Auto-generated method stub
		if (user.getStatus() != OnlineUserDto.STATUS_ONLINE) {
			throw new RuntimeException("user status error");
		}
	}

	/**
	 * @param session
	 * @param message
	 * @param user
	 */
	private void forwardMessage(IoSession session, Object message, OnlineUserDto user) {
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

	private void validateAction(String action) {
		if (OnlineUserDto.ACTION_FAST_JOIN.equals(action) || OnlineUserDto.ACTION_FORWARD.equals(action)
				|| OnlineUserDto.ACTION_GAME_START.equals(action) || OnlineUserDto.ACTION_GET_FRIENDLIST.equals(action)
				|| OnlineUserDto.ACTION_INVITE.equals(action)
				// || OnlineUserDto.ACTION_LOGIN.equals(action) has validated
				// dont do it again
				|| OnlineUserDto.ACTION_LOGOUT.equals(action)) {
			return;
		}
		throw new RuntimeException("action is not invalidate");

	}

	private boolean isValid(LoginData data) {
		if (data == null) {
			return false;
		}

		if (StringUtils.isNotBlank(data.getUsername())) {
			return true;
		}
		return true;
	}

	@Override
	public void messageSent(IoSession session, Object paramObject) throws Exception {
		// TODO Auto-generated method stub

	}

}
