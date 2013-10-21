package com.game.bomb.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;

/**
 * @author CHQ
 * @since  1.0.0
 * @date   2013-8-1
 */
public class User {
	
	
	//不进行序列化
	transient  Long id;
	String username;
	String nickName;
	
	/** md5进行加密*/
	//不进行序列化
	transient String md5Password;
	
	
	//~ new added
	Long experience;
	Integer level;
	Integer portrait;
	Integer heartNum;
	Integer victoryNum;
	Integer loserNum;
	Integer runawayNum;
	Integer medalInUse;
	Integer itemInUse1;
	Integer itemInUse2;
	Integer itemInUse3;
	
	
	@DataProperty(column="experience")
	public Long getExperience() {
		return experience;
	}
	public void setExperience(Long experience) {
		this.experience = experience;
	}
	
	
	@DataProperty(column="level")
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@DataProperty(column="portait")
	public Integer getPortrait() {
		return portrait;
	}
	public void setPortrait(Integer portrait) {
		this.portrait = portrait;
	}
	
	@DataProperty(column="heart_num")
	public Integer getHeartNum() {
		return heartNum;
	}
	public void setHeartNum(Integer heartNum) {
		this.heartNum = heartNum;
	}
	
	@DataProperty(column="victory_num")
	public Integer getVictoryNum() {
		return victoryNum;
	}
	public void setVictoryNum(Integer victoryNum) {
		this.victoryNum = victoryNum;
	}
	
	@DataProperty(column="loser_num")
	public Integer getLoserNum() {
		return loserNum;
	}
	public void setLoserNum(Integer loserNum) {
		this.loserNum = loserNum;
	}
	
	
	@DataProperty(column="runaway_num")
	public Integer getRunawayNum() {
		return runawayNum;
	}
	public void setRunawayNum(Integer runawayNum) {
		this.runawayNum = runawayNum;
	}
	
	@DataProperty(column="medal_in_user")
	public Integer getMedalInUse() {
		return medalInUse;
	}
	public void setMedalInUse(Integer medalInUse) {
		this.medalInUse = medalInUse;
	}
	
	@DataProperty(column="item_in_use1")
	public Integer getItemInUse1() {
		return itemInUse1;
	}
	public void setItemInUse1(Integer itemInUse1) {
		this.itemInUse1 = itemInUse1;
	}
	
	@DataProperty(column="item_in_use2")
	public Integer getItemInUse2() {
		return itemInUse2;
	}
	public void setItemInUse2(Integer itemInUse2) {
		this.itemInUse2 = itemInUse2;
	}
	
	@DataProperty(column="item_in_use3")
	public Integer getItemInUse3() {
		return itemInUse3;
	}
	public void setItemInUse3(Integer itemInUse3) {
		this.itemInUse3 = itemInUse3;
	}
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
