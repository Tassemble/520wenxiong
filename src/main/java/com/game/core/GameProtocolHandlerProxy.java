package com.game.core;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.game.core.bomb.BombMessageHandler;

/**
 * @author CHQ
 * @since 1.0.0
 * @date 2013-7-28
 */
public class GameProtocolHandlerProxy implements IoHandler {

	
	@Autowired
	BombMessageHandler bombMessageHandler;
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		bombMessageHandler.sessionCreated(GameMemory.LOCAL_SESSION_CONTEXT.get().getSession());
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		bombMessageHandler.sessionOpened(GameMemory.LOCAL_SESSION_CONTEXT.get().getSession());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		bombMessageHandler.sessionClosed(GameMemory.LOCAL_SESSION_CONTEXT.get().getSession());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		bombMessageHandler.sessionIdle(GameMemory.LOCAL_SESSION_CONTEXT.get().getSession(), status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		bombMessageHandler.exceptionCaught(GameMemory.LOCAL_SESSION_CONTEXT.get().getSession(), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		bombMessageHandler.messageReceived(GameMemory.LOCAL_SESSION_CONTEXT.get().getSession(), message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		bombMessageHandler.messageSent(GameMemory.LOCAL_SESSION_CONTEXT.get().getSession(), message);
	}

}
