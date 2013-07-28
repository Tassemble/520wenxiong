package com.game.core.dto;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.game.core.exception.NoSuchActionException;
import com.google.gson.Gson;
import com.wenxiong.utils.WordPressUtils;

public class BaseActionDataDto {
	protected String	action;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
	public static class LoginData extends BaseActionDataDto {
		private String	username;
		private String	password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class LogoutData extends BaseActionDataDto {
		private String	username;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}
	
	
	public static class QuitGame extends BaseActionDataDto {
		private String	username;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}


	public static class RoomCreateData extends BaseActionDataDto {
		private Integer	userNumLimit;

		public Integer getUserNumLimit() {
			return userNumLimit;
		}

		public void setUserNumLimit(Integer userNumLimit) {
			this.userNumLimit = userNumLimit;
		}

	}

	public static class FastJoinData extends BaseActionDataDto {
		private Integer	playersNum;
		private int timeoutInSeconds;

		public Integer getPlayersNum() {
			return playersNum;
		}

		public void setPlayersNum(Integer playersNum) {
			this.playersNum = playersNum;
		}

		public int getTimeoutInSeconds() {
			return timeoutInSeconds;
		}

		public void setTimeoutInSeconds(int timeoutInSeconds) {
			this.timeoutInSeconds = timeoutInSeconds;
		}


	}

	// {"action":"forward", "friendList":["a", "b", "c"],
	// "data":"hello i am here"}
	public static class ForwardData extends BaseActionDataDto {
		List<String>	friendList;
		private String	data;

		public List<String> getFriendList() {
			return friendList;
		}

		public void setFriendList(List<String> friendList) {
			this.friendList = friendList;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}

	// {"action":"invite", "friendList":["userA", "userB", "userC"]}
	public static class GameInviteData extends BaseActionDataDto {
		List<String>	friendList;

		public List<String> getFriendList() {
			return friendList;
		}

		public void setFriendList(List<String> friendList) {
			this.friendList = friendList;
		}

	}

	public static void main(String[] args) {
		Gson g = new Gson();
		// new TypeToken<OpenCoursePlay>(){}.getType()
		// ForwardData data =
		// g.fromJson("{\"action\":\"forward\", \"friendList\":[\"a\", \"b\", \"c\"], \"data\":\"hello i am here\"}",
		// JsonDto.ForwardData.class);
		BaseActionDataDto json = (BaseActionDataDto) WordPressUtils.getFromJson(
				"{\"action\":\"getFriendList\"}",
				getClassByAction("getFriendList"));
		System.out.println("action:" + json.getAction());
	}

	// login
	// logout
	// game-start
	// fast-join
	// /forward	
	// getFriendList
	// invite
	public static Class<?> getClassByAction(String action) {
		if (StringUtils.isBlank(action)) {
			return null;
		}
		Class<?> actionClass = ActionNameEnum.getActionClass(action);
		if (actionClass == null) {
			throw new NoSuchActionException(action);
		}
		return actionClass;
	}

}