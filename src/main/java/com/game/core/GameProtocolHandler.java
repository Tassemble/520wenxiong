package com.game.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCache;

import com.game.core.action.processor.ActionAnotationProcessor;
import com.game.core.dispatcher.BaseAction;
import com.game.core.dto.ActionNameEnum;
import com.game.core.dto.BaseActionDataDto;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnDto;
import com.game.core.dto.RoomDto;
import com.game.core.logic.RoomLogic;
import com.game.core.utils.CellLocker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wenxiong.utils.WordPressUtils;

/**
 * 业务处理点入口，除了登录的action在这个类{@link AuthIoFilter}认证之外，其他
 * 所有的action在这里处理，所有的action的类型可以查看{@link ActionNameEnum}
 * 
 * @author CHQ
 * @since 1.0.0
 * @date 2013-7-28
 */
public class GameProtocolHandler implements IoHandler {

	private static final Logger	LOG	= LoggerFactory.getLogger(GameProtocolHandler.class);

	@Autowired
	CellLocker<List<String>>	locker;

	@Autowired
	ListableBeanFactory			listableBeanFactory;
	
	@Autowired
	RoomLogic roomLogic;

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
			roomLogic.doUserQuit(room, user.getUsername());
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
		JsonSessionWrapper jsonSessoin = new JsonSessionWrapper(session);
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

		BaseActionDataDto data = null;
		if (StringUtils.isBlank(action)) {
			// 特殊处理
			MessageSenderHelper.forwardMessageToOtherClientsInRoom(message);
			return;
		}

		// 正常逻辑
		validateAction(action);

		
		// ~ 提供了两种灵活的处理方式：1. 既能处理长连接的方式，2. 也能处理Request-Response的方式(类似http请求)
		if (BaseActionDataDto.getClassByAction(action) != null) {
			data = (BaseActionDataDto) WordPressUtils.getFromJson(message.toString(),
					BaseActionDataDto.getClassByAction(action));

			// 特殊输出，如果是单纯字节的话========================end
			// ~ 这里是第一种方式 能够应付长连接的情况
			Map<String, BaseAction> processorMap = listableBeanFactory.getBeansOfType(BaseAction.class);
			if (!MapUtils.isEmpty(processorMap)) {
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

			// ~ 老代码 需要移植到新的逻辑上去
			if (ActionNameEnum.ACTION_GET_FRIENDLIST.getAction().equals(action)) {
				List<OnlineUserDto> users = Lists.newArrayList();
				for (Entry<String, OnlineUserDto> entry : GameMemory.onlineUsers.entrySet()) {
					users.add(entry.getValue());
				}
				ReturnDto ret = new ReturnDto(200, action, action);
				ret.setResult(users);
				session.write(WordPressUtils.toJson(ret));
				return;
			}
		} else {
			// ~ 处理request-response的方式 非常简单 使用actionAnotation实现
			@SuppressWarnings("unchecked")
			HashMap<String, Object> valueMapper = (HashMap<String, Object>) GameMemory.actionMapping.get(action);
			Map<String, Object> model = Maps.newHashMap();
			if (valueMapper != null) {
				model.put("action", action);
				Method method = (Method) valueMapper.get("method");
				ActionAnotationProcessor processor = (ActionAnotationProcessor) valueMapper.get("object");
				Object returnValue = method.invoke(processor, message, model);
				if (returnValue != null) {
					jsonSessoin.write(model);
				} else {
					//nothing to do
				}
				return;
			}
		}
		throw new NotImplementedException();
	}

	private void validateAction(String action) {

		if (ActionNameEnum.validateAction(action)) {
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
