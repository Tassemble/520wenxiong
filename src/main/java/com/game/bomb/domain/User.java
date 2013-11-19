package com.game.bomb.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import com.wenxiong.blog.commons.domain.BaseDo;

/**
 * @author CHQ
 * @since  1.0.0
 * @date   2013-8-1
 */
public class User extends BaseDo {
	
	
	//不进行序列化
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
	
	//表示使用中的物品
	private String  inUse;
	
	private Boolean enable;
	
	Long inGot;
	Long gold;
	
	
	
	
	@DataProperty(column="in_got")
	public Long getInGot() {
		return inGot;
	}
	public void setInGot(Long inGot) {
		this.inGot = inGot;
	}
	
	public Long getGold() {
		return gold;
	}
	public void setGold(Long gold) {
		this.gold = gold;
	}
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
	
	@DataProperty(column="in_use")
	public String getInUse() {
		return inUse;
	}
	public void setInUse(String inUse) {
		this.inUse = inUse;
	}
	
	@DataProperty(column="enable")
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	
	
	

	
	
	
	
	
	
}
