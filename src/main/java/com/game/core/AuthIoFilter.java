/**
 * 
 */
package com.game.core;

import net.sf.json.JSONObject;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * @author CHQ
 *
 */
public class AuthIoFilter extends IoFilterAdapter {

	
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		
		
		// TODO Auto-generated method stub
		JSONObject json = JSONObject.fromObject(message);
		
		
		
		
		
		super.messageReceived(nextFilter, session, message);
	}
}
