package com.wenxiong.blog.dao;

import org.springframework.test.context.ContextConfiguration;

import com.wenxiong.dao.JunitTransactionSpringContextTest;

@ContextConfiguration(locations = { "classpath:/applicationContext-aop-base.xml",
		"classpath:/applicationContext-dao.xml", "classpath:/applicationContext-service.xml",
		"classpath:/applicationContext-remote.xml", "classpath:/applicationContext-midware.xml",
		"classpath:/applicationContext-bo.xml", "classpath:/biz/applicationContext-framework-aop.xml",
		"classpath:/biz/applicationContext-framework-dao-base.xml" })
public class BaseTestCase extends JunitTransactionSpringContextTest {

}
