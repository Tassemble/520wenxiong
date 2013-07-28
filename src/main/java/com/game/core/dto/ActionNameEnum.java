package com.game.core.dto;

public enum ActionNameEnum {
	FAST_JOIN("fast-join", BaseActionDataDto.FastJoinData.class), 
	MESSAGE_FORWARD("forward", BaseActionDataDto.ForwardData.class),
	ACTION_SYSTEM_BROADCAST("system-broadcast", BaseActionDataDto.class),
	QUIT_GAME("quit-game",BaseActionDataDto.class),
	ACTION_LOGIN("login",BaseActionDataDto.LoginData.class),
	ACTION_LOGOUT("logout",BaseActionDataDto.LogoutData.class),
	ACTION_GAME_START("game-start",BaseActionDataDto.class),
	ACTION_GET_FRIENDLIST("getFriendList",BaseActionDataDto.class),
	ACTION_INVITE("invite",BaseActionDataDto.GameInviteData.class);
	
	
	String action;

	Class<?> actionDataClass;
	
	
	public Class<?> getActionDataClass() {
		return actionDataClass;
	}

	public void setActionDataClass(Class<?> actionDataClass) {
		this.actionDataClass = actionDataClass;
	}


	private ActionNameEnum(String action, Class<?> actionDataClass) {
		this.action = action;
		this.actionDataClass = actionDataClass;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public static boolean validateAction(String action) {
		for (ActionNameEnum existAction : ActionNameEnum.values()) {
			if (existAction.getAction().equals(action)) {
				return true;
			}
		}
		return false;
	}
	
	public static Class<?> getActionClass(String action) {
		for (ActionNameEnum existAction : ActionNameEnum.values()) {
			if (existAction.getAction().equals(action)) {
				return existAction.getActionDataClass();
			}
		}
		return null;
	}
	
	
	
}