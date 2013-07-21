package com.game.core;

import java.nio.charset.Charset;

import org.apache.mina.filter.codec.textline.TextLineCodecFactory;


public class GameTextCodecFilter extends TextLineCodecFactory {

	
	
	public GameTextCodecFilter() {
		super(Charset.forName("UTF-8"));
	}
}
