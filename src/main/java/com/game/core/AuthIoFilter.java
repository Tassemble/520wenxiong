package com.game.core;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.core.dto.ActionNameEnum;
import com.game.core.dto.BaseActionDataDto;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnDto;
import com.game.core.dto.BaseActionDataDto.LoginData;
import com.wenxiong.utils.WordPressUtils;

/**
 * 用户的登录认证在filter中，同时，由于decode的操作不一定一直都是同一线程，但是顺序肯定是
 * 有先后的，这就可以在业务层面中，一进入到filter中时就设置一个threadlocal的变量，一旦执行完
 * filter后就移除这个threadlocal <p>
 * threadlocal这里设置了两个
 * <ul>
 * <li>一个是存放用户的threadlocal</li>
 * <li>一个是存放IoSession的threadlocal</li>
 * </ul>
 * <p>
 * @author CHQ
 * @since  1.0.0
 * @date   2013-7-28
 */
public class AuthIoFilter extends IoFilterAdapter {

	private static final Logger	LOG	= LoggerFactory.getLogger(AuthIoFilter.class);

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		try {
			GameMemory.LOCAL_SESSION.set(session);
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
				if (StringUtils.isBlank(action) || !action.equals(ActionNameEnum.ACTION_LOGIN.getAction())) {
					session.write(WordPressUtils.toJson(new ReturnDto(403, action, "no authentication")));
					return;
				}

				LoginData loginData = (LoginData) WordPressUtils.getFromJson(message.toString(),
						BaseActionDataDto.getClassByAction(action));

				if (isValid(loginData)) {// TODO 实现验证用户名和密码

					OnlineUserDto dto = new OnlineUserDto();
					dto.setSession(session);
					dto.setStatus(OnlineUserDto.STATUS_ONLINE);
					dto.setUsername(loginData.getUsername());
					dto.setNickname(loginData.getUsername());
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
					GameMemory.LOCAL_USER.set(user);
					
					session.write(WordPressUtils.toJson(new ReturnDto(200, action, "logon successfully")));
					return;
				}

				session.write(WordPressUtils.toJson(new ReturnDto(-1, action, "logon failed")));
				return;

			} else {
				
				if (ActionNameEnum.ACTION_LOGIN.getAction().equals(action)) {
					session.write(WordPressUtils.toJson(new ReturnDto(200, action, "you have already logon")));
					return;
				}
				
				GameMemory.LOCAL_USER.set(user);
			}

			super.messageReceived(nextFilter, session, message);

		} finally {
			GameMemory.LOCAL_SESSION.remove();
			GameMemory.LOCAL_USER.remove();
		}

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

}
