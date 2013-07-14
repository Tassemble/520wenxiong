package com.game.core;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @author CHQ
 * @since 20130713
 */
public class GameProtocolHandler implements IoHandler {

	@Override
	public void sessionCreated(IoSession paramIoSession) throws Exception {
		GameMemory.put(String.valueOf(paramIoSession.getId()), paramIoSession);
	}

	@Override
	public void sessionOpened(IoSession paramIoSession) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionClosed(IoSession paramIoSession) throws Exception {
		// TODO Auto-generated method stub
		GameMemory.remove(String.valueOf(paramIoSession.getId()));
	}

	@Override
	public void sessionIdle(IoSession paramIoSession, IdleStatus paramIdleStatus) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exceptionCaught(IoSession paramIoSession, Throwable paramThrowable) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageReceived(IoSession paramIoSession, Object paramObject) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageSent(IoSession paramIoSession, Object paramObject) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	
}
