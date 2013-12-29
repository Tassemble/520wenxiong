package com.game.bomb.domain;

import com.wenxiong.blog.commons.domain.BaseDo;

public class GameAttribute extends BaseDo {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String	attrValue;
	String	attrName;
	
	
	
	public static final String KEY_DURATION_OF_RENEW_BLOOD = "key_duration_of_renew_blood";  //单位是ms 毫秒

	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

}
