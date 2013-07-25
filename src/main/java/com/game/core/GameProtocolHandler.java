package com.game.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.game.core.dispatcher.BaseAction;
import com.game.core.dto.JsonDto;
import com.game.core.dto.JsonDto.BaseJsonData;
import com.game.core.dto.JsonDto.LoginData;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.OnlineUserVo;
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

	private static final Logger	LOG	= LoggerFactory.getLogger(GameProtocolHandler.class);

	@Autowired
	CellLocker<List<String>>	locker;

	@Autowired
	ListableBeanFactory			listableBeanFactory;

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
		// locker.lock("", key);
		OnlineUserDto user = GameMemory.sessionUsers.get(session.getId());
		if (user == null) {
			return;
		}
		RoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		if (room != null) {
			List<String> key = Arrays.asList(String.valueOf(room.getId()));
			try {
				locker.lock("", key);
				room.getUsers().remove(user);
				room.decreaseCnt();

				if (room.isEmpty()) {
					room.setRoomStatus(RoomDto.ROOM_STATUS_OPEN);
				} else {
					MessageSenderHelper.forwardMessageInRoom(WordPressUtils.toJson(new ReturnDto(200,
							OnlineUserDto.ACTION_SYSTEM_BROADCAST, user.getUsername() + " quit game (" + room.getId()
									+ "), num of players: " + room.getCntNow())));
				}
			} finally {
				locker.unLock("", key);
			}
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

		BaseJsonData data = null;
		if (StringUtils.isBlank(action)) {
			// 特殊处理
			MessageSenderHelper.forwardMessageInRoom(message);
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

		// if (OnlineUserDto.ACTION_FORWARD.equals(action)) {
		// MessageSenderHelper.forwardMessage(session, message, user);
		// return;
		// }

		Map<String, BaseAction> processorMap = listableBeanFactory.getBeansOfType(BaseAction.class);
		if (processorMap != null) {
			Collection<BaseAction> processors = processorMap.values();
			if (!CollectionUtils.isEmpty(processors)) {
				for (BaseAction processor : processors) {
					if (processor.getAction().equals(action)) {
						processor.doAction(session, data);
						return;// one time process one thing
					}
				}
			}
		}

		// if (OnlineUserDto.ACTION_FAST_JOIN.equals(action)) {
		//
		// doFastJoinAction(session, data);
		// return;
		// }

		if (OnlineUserDto.ACTION_GET_FRIENDLIST.equals(action)) {
			List<OnlineUserVo> users = Lists.newArrayList();
			for (Entry<String, OnlineUserDto> entry : GameMemory.onlineUsers.entrySet()) {
				users.add(new OnlineUserVo(entry.getValue()));
			}
			ReturnDto ret = new ReturnDto(200, action, action);
			ret.setResult(users);
			session.write(WordPressUtils.toJson(ret));
			return;
		}

		throw new NotImplementedException();
	}

	/**
	 * @param session
	 * @param action
	 * @param user
	 * @param data
	 */
	private void doFastJoinAction(IoSession session, BaseJsonData data) {

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

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if (LOG.isDebugEnabled()) {
			if (message != null)
				LOG.debug("sent:" + message.toString());
		}

	}

}
