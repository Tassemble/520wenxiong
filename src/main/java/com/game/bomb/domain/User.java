package com.game.bomb.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;

/**
 * @author CHQ
 * @since  1.0.0
 * @date   2013-8-1
 */
public class User {
	
	
	
	Long id;
	String username;
	String nickName;
	
	/** md5进行加密*/
	String md5Password;
	
	@DataProperty(column="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@DataProperty(column="username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@DataProperty(column="nickname")
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	@DataProperty(column="password")
	public String getMd5Password() {
		return md5Password;
	}
	public void setMd5Password(String md5Password) {
		this.md5Password = md5Password;
	}

	
	
	
	
	
	
}
