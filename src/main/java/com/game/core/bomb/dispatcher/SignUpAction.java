package com.game.core.bomb.dispatcher;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;
import com.game.core.dto.ActionNameEnum;
import com.game.core.dto.BaseActionDataDto;
import com.game.core.dto.BaseActionDataDto.GameSignUpData;
import com.game.core.dto.ReturnDto;
import com.game.core.exception.ActionFailedException;
import com.wenxiong.utils.GsonUtils;

@Component
public class SignUpAction implements BaseAction{

	
	@Autowired
	UserService userService;
	
	
	@Override
	public void doAction(IoSession session, BaseActionDataDto baseData) {
		BaseActionDataDto.GameSignUpData data = (BaseActionDataDto.GameSignUpData)baseData;
		
		validate(data);
		
		User query = new User();
		query.setUsername(data.getUsername());
		List<User> users = userService.getByDomainObjectSelective(query);
		if (!CollectionUtils.isEmpty(users)) {
			session.write(new ReturnDto(-1, this.getAction(), "user existed"));
			return;
		}
		
		User newItem = query;
		Date now = new Date();
		
		
		newItem.setMd5Password(DigestUtils.md5Hex(data.getPassword()));
		newItem.setId(userService.getId());
		newItem.setNickName(data.getNickname());
		newItem.setHeartNum(0);
		newItem.setLevel(1);
		newItem.setLoserNum(0);
		newItem.setPortrait(0);
		newItem.setRunawayNum(0);
		newItem.setVictoryNum(0);
		newItem.setGmtCreate(now);
		newItem.setGmtModified(now);
		userService.add(query);
		session.write(new ReturnDto(200, this.getAction(), "signup successfully"));
		return;
	}

	private void validate(GameSignUpData data) {
		if (data == null || StringUtils.isBlank(data.getNickname()) || 
				StringUtils.isBlank(data.getUsername()) ||
				StringUtils.isBlank(data.getPassword()))
			throw new ActionFailedException(data.getAction());
	}

	@Override
	public String getAction() {
		return ActionNameEnum.ACTION_SIGN_UP.getAction();
	}

}
