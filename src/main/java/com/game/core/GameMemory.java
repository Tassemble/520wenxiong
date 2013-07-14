package com.game.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class GameMemory {

	static Map<String, Object> map;
	
	static {
		map = new ConcurrentHashMap<String, Object>();
	}
	
	
	public static void put(String key, Object value) {
		map.put(key, value);
	}
	
	
	public static Object get(String key) {
		return map.get(key);
	}
	
	
	public static void remove(String key) {
		map.remove(key);
	}
	
}
