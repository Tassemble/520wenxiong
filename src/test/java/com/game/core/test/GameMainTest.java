package com.game.core.test;

import java.io.IOException;

import org.junit.Test;

import com.game.core.GameMain;
import com.wenxiong.utils.WordPressUtils;

public class GameMainTest {
	
	@Test
	public void testRun() throws IOException {
		GameMain.startWithSpring();
	}
	
	
	@Test
	public void testDecodeMsg(){
		System.out.println(WordPressUtils.toJson("{\"action\":\"forward\",\"code\":200,\"message\":\"AQAAAGxqZW1oamVsZmZmZWxkZGZmZWVobW1nbWVtZGZobWxpbGRmZGtkZWVnaGZoZGtsZWxsZWVnamVtZmpkZG1sZW1qbW1pZm1nZmVtZmlkZWVlbGdqZWhmbWZlbGhmZm1qZm1laGZsZ2loaGtnZ2lsZGRkZmxtZWdkaGhmZW1kaGZnZW1oZmRrZWRqZGZtbG1tZ2xsZW1nZWZpbGVlZGxoZWhnbGxlaWhkZWs=\"}"));
	}
}
