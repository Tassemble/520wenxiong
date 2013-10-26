package com.game.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.game.core.GameMemory;
import com.game.core.action.bomb.CommonProcessor;
import com.game.core.action.processor.PlayerInfoProcessorHelper;
import com.wenxiong.blog.dao.BaseTestCase;
import com.wenxiong.utils.WordPressUtils;

public class CommonProcessorTest extends BaseTestCase{

	
	@Autowired
	CommonProcessor commonProcessor;
	
	@Autowired
	PlayerInfoProcessorHelper playerInfoProcessorHelper;
	
	
	public Properties readProperties() throws IOException {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();           
		InputStream stream = loader.getResourceAsStream("test.properties");
		prop.load(stream);
		return prop;
	}
	
	@Test
	public void uploadPlayerInfoTest() throws IOException {
		Properties prop = readProperties();
		setOnlineUser(3L);
		GameMemory.getUser().setAction("uploadPlayerInfo");
		commonProcessor.uploadPlayerInfo(prop.get("uploadPlayerInfo"), new HashMap<String, Object>());
	}
	
	@Test
	public void downloadPlayerInfoTest() throws Exception {
		setOnlineUser(3L);
		GameMemory.getUser().setAction("downloadPlayerInfo");
		Map map = new HashMap<String, Object>();
		playerInfoProcessorHelper.innerDownloadPlayerInfo(map);
		
		System.out.println(WordPressUtils.toJson(map));
	}
}
