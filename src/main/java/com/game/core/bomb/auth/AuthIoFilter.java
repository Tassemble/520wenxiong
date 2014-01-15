package com.game.core.bomb.auth;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.game.bomb.constant.LoginConstant;
import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.GameVersionDto;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.JsonSessionWrapper;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.BaseActionDataDto;
import com.game.core.bomb.dto.BaseActionDataDto.GameSignUpData;
import com.game.core.bomb.dto.BaseActionDataDto.LoginData;
import com.game.core.bomb.dto.GameSessionContext;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.exception.BombException;
import com.game.utils.GsonUtils;

/**
 * 用户的登录认证在filter中，同时，由于decode的操作不一定一直都是同一线程，但是顺序肯定是
 * 有先后的，这就可以在业务层面中，一进入到filter中时就设置一个threadlocal的变量，一旦执行完 filter后就移除这个threadlocal
 * <p>
 * threadlocal这里设置了两个
 * <ul>
 * <li>一个是存放用户的threadlocal</li>
 * <li>一个是存放IoSession的threadlocal</li>
 * </ul>
 * <p>
 * 
 * @author CHQ
 * @since 1.0.0
 * @date 2013-7-28
 */
public class AuthIoFilter extends IoFilterAdapter {

	private static final Logger	LOG		= LoggerFactory.getLogger(AuthIoFilter.class);

	@Autowired
	UserService					userService;

	final boolean				isMock	= false;

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		String action = null;
		try {
			// 特殊输出，如果是单纯字节的话========================start
			JSONObject json = null;
			try {
				json = JSONObject.fromObject(message);
				action = json.getString("action");
			} catch (Exception e) {
				LOG.warn("sessionID:" + session.getId()+" parse json exeception, message:" + json, e);
			}
			
		
			
			
			
			JsonSessionWrapper jsonSession = new JsonSessionWrapper(session);;
			
			GameSessionContext context = new GameSessionContext();
			context.setSession(jsonSession);
			
			
			//process update action
			if ("getVersion".equals(action)) {
				jsonSession.write(new GameVersionDto(action));
				return;
			}
			
			
			
			
			GameMemory.LOCAL_SESSION_CONTEXT.set(context);
			GameMemory.LOCAL_SESSION_CONTEXT.get().setAction(action);

			OnlineUserDto user = GameMemory.SESSION_USERS.get(jsonSession.getId());
			if (user == null) {

				
				if (!StringUtils.isBlank(action) && action.equals(ActionNameEnum.ACTION_SIGN_UP.getAction())) {//except
					super.messageReceived(nextFilter, session, message);
					return;
				}
				
				
				
				// is login?
				if (StringUtils.isBlank(action) || !(action.equals(ActionNameEnum.ACTION_LOGIN.getAction())
						|| action.equals("sinalogin")) ) 
				{
					jsonSession.write(new ReturnDto(403, action, "no authentication"));
					return;
				}
				
				String loginType = "";
				if (ActionNameEnum.ACTION_LOGIN.getAction().equals(action)) {
					loginType = LoginConstant.LOGIN_TYPE_DEFAULT;
				} else if (ActionNameEnum.ACTION_SINA_LOGIN.getAction().equals(action)) {
					loginType = LoginConstant.LOGIN_TYPE_SINA;
				} else {
					jsonSession.write(new ReturnDto(201, action, "function not implemented"));
					return;
				}

				OnlineUserDto dto = login(message.toString(), loginType);
				if (dto != null) {// TODO 实现验证用户名和密码
					dto.setSession(jsonSession);

					if (GameMemory.ONLINE_USERS.containsValue(dto)) {
						OnlineUserDto oldUser = GameMemory.ONLINE_USERS.get(dto.getId());
						GameMemory.ONLINE_USERS.remove(dto);
						oldUser.getSession().write("remote client logon");
						oldUser.getSession().close();
					//	if (session.getId() != oldUser.getSession().getId()) {
					//		session.write(WordPressUtils.toJson(new ReturnDto(
					//				ReturnDto.ALREADY_LOGON_CODE_WITH_OTHER_REMOTE_CLIENT, action,
					//				"you have already logon in remote clinet")));
					//	} else {
					//		session.write(WordPressUtils.toJson(new ReturnDto(ReturnDto.ALREADY_LOGON_CODE, action,
					//				"you have already logon")));
					//	}
					//	return;
					}
					LOG.info("validate ok for username:" + dto.getUsername());
					GameMemory.ONLINE_USERS.put(dto.getId(), dto);
					GameMemory.SESSION_USERS.put(jsonSession.getId(), dto);
					GameMemory.setUser(user);

					jsonSession.write(new ReturnDto(200, action, "logon successfully"));
					return;
				}

				jsonSession.write(new ReturnDto(-1, action, "logon failed"));
				return;

			} else {

				if (ActionNameEnum.ACTION_LOGIN.getAction().equals(action)) {
					//如果用户
					jsonSession.write(new ReturnDto(200, action, "you have already logon"));
					return;
				}

				GameMemory.setUser(user);
			}

			super.messageReceived(nextFilter, session, message);

		} catch(Exception  e) {
			if (BombException.class.isAssignableFrom(e.getClass())) {
				BombException exception = (BombException)e;
				exception.setAction(action);
				throw exception;
			} else {
				throw e;
			}
		}
		finally {
			GameMemory.LOCAL_SESSION_CONTEXT.remove();
		}

	}
	
	




	public OnlineUserDto login(String message, String loginType) {
		JSONObject json = JSONObject.fromObject(message);
		String action = json.getString("action");
		if (LoginConstant.LOGIN_TYPE_DEFAULT.equals(loginType)) {
			LoginData loginData = (LoginData) GsonUtils.getFromJson(message.toString(),
					BaseActionDataDto.getClassByAction(action));
			loginData.setLoginType(loginType);
			return validateLogin(loginData);
		} else if (LoginConstant.LOGIN_TYPE_SINA.equals(loginType)) {
			//TODO check token if is validate
			
			
			String username = json.getJSONObject("data").getString("userid");
			String nickname = json.getJSONObject("data").getString("nickname");
			
			
			User query = new User();
			query.setUsername(username); //是否有注入的可能性
			query.setLoginType(loginType);
			List<User> users = userService.getByDomainObjectSelective(query);
			if (CollectionUtils.isEmpty(users)) { //no users register
				GameSignUpData signUpData = new GameSignUpData();
				signUpData.setUsername(username);
				signUpData.setAction(action);
				signUpData.setNickname(nickname);
				signUpData.setLoginType(loginType);
				userService.addNewUser(signUpData); // add new one
				
				
				users = userService.getByDomainObjectSelective(query); //query again
				OnlineUserDto dto = new OnlineUserDto(users.get(0));
				dto.setStatus(OnlineUserDto.STATUS_ONLINE);
				return dto;
				
			} else {
				OnlineUserDto dto = new OnlineUserDto(users.get(0));
				dto.setStatus(OnlineUserDto.STATUS_ONLINE);
				return dto;
			}
		} else {
			return null;
		}
	}

	private OnlineUserDto validateLogin(LoginData loginData) {
		if (StringUtils.isBlank(loginData.getUsername()) || StringUtils.isBlank(loginData.getPassword())) {
			return null;
		}

		OnlineUserDto dto = new OnlineUserDto();
		dto.setStatus(OnlineUserDto.STATUS_ONLINE);
		dto.setUsername(loginData.getUsername());
		dto.setNickname(loginData.getUsername());
		

		if (isMock) {
			return dto;
		} else {
			User query = new User();
			query.setMd5Password(DigestUtils.md5Hex(loginData.getPassword()));
			query.setUsername(loginData.getUsername()); //是否有注入的可能性
			query.setLoginType(loginData.getLoginType());
			List<User> users = userService.getByDomainObjectSelective(query);
			if (!CollectionUtils.isEmpty(users)) {
				dto = new OnlineUserDto(users.get(0));
				dto.setStatus(OnlineUserDto.STATUS_ONLINE);
				return dto;
			}
		}
		return null;
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
