package com.game.core.dispatcher;

import org.apache.mina.core.session.IoSession;

import com.game.core.dto.JsonDto.BaseJsonData;

public interface BaseAction {
	
	public final static String FAST_JOIN = "fast-join";

	public void doAction(IoSession session, BaseJsonData data);
	
	public String getAction();
	
}
