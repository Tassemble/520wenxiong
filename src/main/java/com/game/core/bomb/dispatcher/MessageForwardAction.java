package com.game.core.bomb.dispatcher;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.MessageSenderHelper;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.BaseActionDataDto;
import com.game.core.bomb.dto.BaseActionDataDto.ForwardData;
import com.game.core.bomb.logic.RoomLogic;
import com.google.common.collect.Maps;


@Component
public class MessageForwardAction implements BaseAction {

	
	@Override
	public void doAction(IoSession session, BaseActionDataDto baseData) {
		ForwardData data = (ForwardData) baseData;
		if (!CollectionUtils.isEmpty(data.getFriendList())) {
			for (String friend : data.getFriendList()) {
				IoSession friendSession = GameMemory.getSessionByUsername(friend);
				Map<String, Object> map = Maps.newHashMap();
				map.put("code", 200);
				map.put("action", this.getAction());
				map.put("data", data.getData());
				MessageSenderHelper.forwardMessage(friendSession, map);
			}
		} else {
			RoomLogic.forwardMessageToOtherClientsInRoom(data.getData());
		}
	}

	@Override
	public String getAction() {
		return ActionNameEnum.MESSAGE_FORWARD.getAction();
	}

}
