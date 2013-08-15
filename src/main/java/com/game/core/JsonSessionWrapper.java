package com.game.core;

import org.apache.mina.core.session.IoSession;

import com.wenxiong.utils.WordPressUtils;

public class JsonSessionWrapper {

	IoSession session;
	
	public JsonSessionWrapper(IoSession session) {
		this.session = session;
	}
	public void write(Object object) {
		String json = WordPressUtils.toJson(object);
		session.write(json);
	}
}
