package com.game.core.dto;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.wenxiong.utils.WordPressUtils;

public class JsonDto {

	public static class LoginData extends BaseJsonData {
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

	public static class LogoutData extends BaseJsonData {
		private String	username;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}

	public static class RoomCreateData extends BaseJsonData {
		private Integer	userNumLimit;

		public Integer getUserNumLimit() {
			return userNumLimit;
		}

		public void setUserNumLimit(Integer userNumLimit) {
			this.userNumLimit = userNumLimit;
		}

	}

	public static class FastJoinData extends BaseJsonData {
		private Integer	maxplayersnum;
		
		private Integer	minplayersnum;

		public Integer getMaxplayersnum() {
			return maxplayersnum;
		}

		public void setMaxplayersnum(Integer maxplayersnum) {
			this.maxplayersnum = maxplayersnum;
		}

		public Integer getMinplayersnum() {
			return minplayersnum;
		}

		public void setMinplayersnum(Integer minplayersnum) {
			this.minplayersnum = minplayersnum;
		}
		
		

	}

	// {"action":"forward", "friendList":["a", "b", "c"],
	// "data":"hello i am here"}
	public static class ForwardData extends BaseJsonData {
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
	public static class GameInviteData extends BaseJsonData {
		List<String>	friendList;

		public List<String> getFriendList() {
			return friendList;
		}

		public void setFriendList(List<String> friendList) {
			this.friendList = friendList;
		}

	}

	// {"action":"getFriendList"}
	// {"action":"game-start"}
	public static class BaseJsonData {
		private String	action;

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}
	}

	public static void main(String[] args) {
		Gson g = new Gson();
		// new TypeToken<OpenCoursePlay>(){}.getType()
		// ForwardData data =
		// g.fromJson("{\"action\":\"forward\", \"friendList\":[\"a\", \"b\", \"c\"], \"data\":\"hello i am here\"}",
		// JsonDto.ForwardData.class);
		BaseJsonData json = (BaseJsonData) WordPressUtils.getFromJson(
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

		if (action.equalsIgnoreCase("login")) {
			return JsonDto.LoginData.class;
		}

		if (action.equalsIgnoreCase("logout")) {
			return JsonDto.LogoutData.class;
		}

		if (action.equalsIgnoreCase("game-start")) {
			return JsonDto.BaseJsonData.class;
		}

		if (action.equalsIgnoreCase("fast-join")) {
			return JsonDto.FastJoinData.class;
		}

		if (action.equalsIgnoreCase("forward")) {
			return JsonDto.ForwardData.class;
		}

		if (action.equalsIgnoreCase("invite")) {
			return JsonDto.GameInviteData.class;
		}
		if (action.equalsIgnoreCase("game-start")) {
			return JsonDto.BaseJsonData.class;
		}
		
		if (action.equalsIgnoreCase("getFriendList")) {
			return JsonDto.BaseJsonData.class;
		}
		
		
		throw new IllegalArgumentException("action:" + action);

	}

}
