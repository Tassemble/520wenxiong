package com.game.bomb.service.impl;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.bomb.Dao.UserDao;
import com.game.bomb.Dao.WealthBudgetDao;
import com.game.bomb.domain.User;
import com.game.bomb.domain.WealthBudget;
import com.game.bomb.service.UserService;
import com.game.core.bomb.dto.BaseActionDataDto.GameSignUpData;
import com.wenxiong.blog.commons.dao.BaseDao;
import com.wenxiong.blog.commons.service.impl.BaseServiceImpl;

/**
 * @author CHQ
 * @since 2013-8-1 
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<BaseDao<User>, User> implements UserService{

	UserDao userDao;
	
	
	@Autowired
	WealthBudgetDao wealthBudgetDao;

	public UserDao getUserDao() {
		return userDao;
	}

	
	@Autowired
	public void setUserDao(UserDao userDao) {
		super.setBaseDao(userDao);
		this.userDao = userDao;
	}


	@Override
	public void addNewUser(GameSignUpData data) {
		User newItem = new User();
		Date now = new Date();
		
		newItem.setUsername(data.getUsername());
		newItem.setMd5Password(DigestUtils.md5Hex(data.getPassword()));
		newItem.setNickName(data.getNickname());
		newItem.setHeartNum(5);
		newItem.setLevel(1);
		newItem.setLoserNum(0);
		newItem.setPortrait(0);
		newItem.setRunawayNum(0);
		newItem.setVictoryNum(0);
		newItem.setGmtCreate(now);
		newItem.setGmtModified(now);
		newItem.setGold(WealthBudget.DEFAULT_WEALTH);
		newItem.setInGot(WealthBudget.DEFAULT_WEALTH);
		add(newItem);
		
		User qUser = new User();
		qUser.setUsername(data.getUsername());
		User userFromDB = getByDomainObjectSelective(qUser).get(0);
		
		WealthBudget wealth = new WealthBudget();
		wealth.setBudgetType(WealthBudget.BUDGET_TYPE_SIGNUP);
		wealth.setGmtCreate(now);
		wealth.setGmtModified(now);
		wealth.setOrderId(0L);
		wealth.setQuantity(WealthBudget.DEFAULT_WEALTH);
		wealth.setUid(userFromDB.getId());
		wealthBudgetDao.add(wealth);
	}
	
	
	
	
}
