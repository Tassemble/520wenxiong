package com.wenxiong.blog.dao;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.dto.OnlineUserDto;
import com.wenxiong.dao.JunitTransactionSpringContextTest;

@ContextConfiguration(locations = { "classpath:/applicationContext-aop-base.xml",
		"classpath:/applicationContext-dao.xml", "classpath:/applicationContext-service.xml",
		"classpath:/applicationContext-remote.xml", "classpath:/applicationContext-midware.xml",
		"classpath:/applicationContext-bo.xml", "classpath:/biz/applicationContext-framework-aop.xml",
		"classpath:/biz/applicationContext-framework-dao-base.xml" })
public class BaseTestCase extends JunitTransactionSpringContextTest {

	
	@Autowired
	UserService service;
	
	public void setOnlineUser(Long id) {
		User user = service.getById(id);
		OnlineUserDto onlineUser = new OnlineUserDto(user);
		GameMemory.setUser(onlineUser);
		GameMemory.onlineUsers.put(user.getUsername(), onlineUser);
	}
}
