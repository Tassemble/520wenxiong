package com.game.core.dispatcher;

import org.apache.mina.core.session.IoSession;

import com.game.core.dto.JsonDto.BaseJsonData;

public interface BaseAction {
	
	public final static String FAST_JOIN = "fast-join";
	
	public final static String MESSAGE_FORWARD = "forward";

	public void doAction(IoSession session, BaseJsonData baseData);
	
	public String getAction();
	
}