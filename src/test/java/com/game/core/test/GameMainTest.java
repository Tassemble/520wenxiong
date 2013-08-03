package com.game.core.test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.mina.core.service.IoAcceptor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;

import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;
import com.game.core.GameMain;
import com.wenxiong.blog.dao.BaseTestCase;
import com.wenxiong.utils.WordPressUtils;

public class GameMainTest extends BaseTestCase{
	
	
	@Autowired
	UserService userService;
	
	@Autowired
	ApplicationContext ctx;
	
	
	@Test
	public void testRun() throws IOException {
		
		IoAcceptor acceptor = (IoAcceptor) ctx.getBean("ioAcceptor");
		try {
			while (acceptor.isActive()) {
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			acceptor.unbind();
		}
		
		
	}
	
	
	@Test
	public void testDecodeMsg(){
		System.out.println(WordPressUtils.toJson("{\"action\":\"forward\",\"code\":200,\"message\":\"AQAAAGxqZW1oamVsZmZmZWxkZGZmZWVobW1nbWVtZGZobWxpbGRmZGtkZWVnaGZoZGtsZWxsZWVnamVtZmpkZG1sZW1qbW1pZm1nZmVtZmlkZWVlbGdqZWhmbWZlbGhmZm1qZm1laGZsZ2loaGtnZ2lsZGRkZmxtZWdkaGhmZW1kaGZnZW1oZmRrZWRqZGZtbG1tZ2xsZW1nZWZpbGVlZGxoZWhnbGxlaWhkZWs=\"}"));
	}
	
	
	@Test
	public void testAddUser() {
		User u = new User();
		u.setId(userService.getId());
		u.setMd5Password(DigestUtils.md5Hex("FirstUser"));
		u.setUsername("CHQ");
		u.setNickName("CHQ");
		userService.add(u);
		
		
		User query = new User();
		query.setMd5Password(DigestUtils.md5Hex("FirstUser"));
		query.setUsername("CHQ");
		List<User> users = userService.getByDomainObjectSelective(query);
		if (!CollectionUtils.isEmpty(users)) {
			System.out.println("find CHQ");
		}
		
	}
}
