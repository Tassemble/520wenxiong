package com.game.core.dispatcher;

import org.apache.mina.core.session.IoSession;

import com.game.core.dto.BaseActionDataDto;

public interface BaseAction {
	
	
	public void doAction(IoSession session, BaseActionDataDto baseData);
	
	public String getAction();
	
}
