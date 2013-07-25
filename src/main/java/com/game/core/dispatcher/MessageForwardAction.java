package com.game.core.dispatcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.MessageSenderHelper;
import com.game.core.dto.JsonDto.BaseJsonData;
import com.game.core.dto.JsonDto.ForwardData;


@Component
public class MessageForwardAction implements BaseAction {

	@Override
	public void doAction(IoSession session, BaseJsonData baseData) {
		ForwardData data = (ForwardData) baseData;
		if (!CollectionUtils.isEmpty(data.getFriendList())) {
			for (String friend : data.getFriendList()) {
				IoSession friendSession = GameMemory.getSessionByUsername(friend);
				MessageSenderHelper.forwardMessage(friendSession, data.getData());
			}
		} else {
			MessageSenderHelper.forwardMessage(session, data.getData(),
					GameMemory.getOnlineUserBySessionId(session.getId()));
		}
	}

	@Override
	public String getAction() {
		return MESSAGE_FORWARD;
	}

}
