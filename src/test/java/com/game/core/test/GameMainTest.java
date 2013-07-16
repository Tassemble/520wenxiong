package com.game.core.test;

import java.io.IOException;

import org.junit.Test;

import com.game.core.GameMain;

public class GameMainTest {
	
	@Test
	public void testRun() throws IOException {
		GameMain.start();
	}
}
