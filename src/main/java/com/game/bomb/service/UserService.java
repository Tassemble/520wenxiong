package com.game.bomb.service;

import com.game.base.commons.service.BaseService;
import com.game.bomb.domain.User;
import com.game.core.bomb.dto.BaseActionDataDto.GameSignUpData;


/**
 * @author CHQ
 * @since 2013-8-1 
 */
public interface UserService extends BaseService<User>{

	void addNewUser(GameSignUpData data);
	
	
	void updateUserBloodWithLock(User update);


	void updateForExchangeCoinToHeart(Long id, int number, int gainHeart);

}
