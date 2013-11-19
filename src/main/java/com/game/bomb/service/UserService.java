package com.game.bomb.service;

import com.game.bomb.domain.User;
import com.game.core.bomb.dto.BaseActionDataDto.GameSignUpData;
import com.wenxiong.blog.commons.service.BaseService;


/**
 * @author CHQ
 * @since 2013-8-1 
 */
public interface UserService extends BaseService<User>{

	void addNewUser(GameSignUpData data);

}
