package com.game.bomb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.bomb.Dao.UserDao;
import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;
import com.wenxiong.blog.commons.dao.BaseDao;
import com.wenxiong.blog.commons.service.impl.BaseServiceImpl;

/**
 * @author CHQ
 * @since 2013-8-1 
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<BaseDao<User>, User> implements UserService{

	UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	
	@Autowired
	public void setUserDao(UserDao userDao) {
		super.setBaseDao(userDao);
		this.userDao = userDao;
	}
	
	
	
	
	
}
