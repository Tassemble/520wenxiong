package com.game.bomb.mobile.dto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.game.bomb.domain.User;
import com.game.core.GameMemory;
import com.game.core.bomb.dto.OnlineUserDto;

/**
 * 需要具备如下属性： 玩家昵称（String) 玩家ID（String) 用户状态（离线，在线，游戏中）（String） 用户头像（unsigned
 * int） 玩家等级（unsigned int) 胜率(float)( [0.0 , 1.0] ) 逃跑率(float)( [0.0 , 1.0] )
 * 这些封装到对象Map中 使用中的勋章1（unsigned int) 使用中的勋章2（unsigned int) 使用中的勋章3（unsigned int)
 * 使用中的商品1（Dictionary)(unsigned int,unsigned int) 使用中的商品2（unsigned int)(unsigned
 * int,unsigned int) 使用中的商品3（unsigned int)(unsigned int,unsigned int)
 * 
 * @author CHQ
 * @date Oct 24, 2013
 * @since 1.0
 */
public class MobileUserDto {

	Long				id;
	String				username;
	String				nickName;
	Integer				portrait;
	Integer				level;
	Float				win;
	Float				runaway;
	Float 				lose;
	

	
	
	//should set it alone
	String				status;
	Map<Object, Object>	inUse;
	
	
	public MobileUserDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.nickName = user.getNickName();
		this.portrait = user.getPortrait();
		this.level = user.getLevel();
		int victoryNum = user.getVictoryNum() == null ? 0 : user.getVictoryNum();
		int loseNum = user.getLoserNum() == null ? 0 : user.getLoserNum();
		int runawayNum = user.getRunawayNum() == null ? 0 : user.getRunawayNum();
		
		int total =  victoryNum + loseNum + runawayNum;
		if (total == 0) {
			this.win = 0.0f;
			this.runaway = 0.0f;
			this.lose = 0.0f;
		} else {
			this.win = (float)victoryNum / (float)(total);
			this.runaway = (float)runawayNum/(float)(total);
			this.lose = (float)loseNum /(float)(total);
		}
	}
	

	@SuppressWarnings("unchecked")
	public MobileUserDto(OnlineUserDto user) throws Exception {
		this.id = user.getId();
		this.username = user.getUsername();
		this.nickName = user.getNickname();
		this.portrait = user.getPortrait();
		this.level = user.getLevel();
		int victoryNum = user.getVictoryNum() == null ? 0 : user.getVictoryNum();
		int loseNum = user.getLoserNum() == null ? 0 : user.getLoserNum();
		int runawayNum = user.getRunawayNum() == null ? 0 : user.getRunawayNum();
		int total = victoryNum + loseNum + runawayNum;
		if (total == 0) {
			this.win = 0.0f;
			this.lose = 0.0f;
			this.runaway = 0.0f;
		} else {
			this.win = (float)victoryNum / (float)(total);
			this.runaway = (float)runawayNum/(float)(total);
			this.lose = (float)loseNum /(float)(total);
		}
		this.status = user.getStatus();
		if (!StringUtils.isBlank(user.getInUse())) {
			this.inUse = new ObjectMapper().readValue(user.getInUse(), HashMap.class);
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public static MobileUserDto buildMobileUser(User user) throws JsonParseException, JsonMappingException, IOException {
		OnlineUserDto onlineUser = GameMemory.getUserByUsername(user.getUsername());
		
		if (onlineUser == null) {
			return null;
		}
		MobileUserDto mobData = new MobileUserDto(user);
		mobData.setStatus(onlineUser.getStatus());
		if (!StringUtils.isBlank(user.getInUse())) {
			mobData.setInUse(new ObjectMapper().readValue(user.getInUse(), HashMap.class));
		}
		return mobData;
	}
	
	
	
	
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPortrait() {
		return portrait;
	}

	public void setPortrait(Integer portrait) {
		this.portrait = portrait;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Float getWin() {
		return win;
	}

	public void setWin(Float win) {
		this.win = win;
	}

	public Float getRunaway() {
		return runaway;
	}

	public void setRunaway(Float runaway) {
		this.runaway = runaway;
	}

	public Map<Object, Object> getInUse() {
		return inUse;
	}

	public void setInUse(Map<Object, Object> inUse) {
		this.inUse = inUse;
	}

}
