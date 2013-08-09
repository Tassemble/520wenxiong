package com.game.core.action.processor;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.domain.FriendRelation;
import com.game.bomb.domain.User;
import com.game.bomb.service.FriendRelationService;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.annotation.ActionAnnotation;
import com.game.core.dto.BaseActionDataDto;
import com.game.core.dto.BaseActionDataDto.UploadPlayerData;
import com.game.core.dto.OnlineUserDto;
import com.game.core.exception.NoAuthenticationException;
import com.google.common.collect.Maps;
import com.wenxiong.blog.commons.utils.collection.PropertyExtractUtils;

@Component
public class CommonProcessor implements ActionAnotationProcessor {

	@Autowired
	UserService				userService;

	@Autowired
	FriendRelationService	friendRelationService;

	@ActionAnnotation(action = "downloadPlayerInfo")
	public Map<String, Object> downloadPlayerInfo(BaseActionDataDto baseData) {
		Map<String, Object> map = Maps.newHashMap();
		OnlineUserDto onlineUser = GameMemory.getUser();
		if (onlineUser == null) {
			throw new NoAuthenticationException("downloadPlayerInfo");
		}
		User user = userService.getById(onlineUser.getId());
		map.put("self", user);
		// ~ put init
		map.put("friends", null);
		// ~ get friends
		List<FriendRelation> relations = friendRelationService.getByCondition("user_id = ? and status = ?",
				onlineUser.getId(), FriendRelation.STATUS_ACCEPTED);
		if (!CollectionUtils.isEmpty(relations)) {
			List<Long> friendIds = PropertyExtractUtils.getByPropertyValue(relations, "FriendId");
			List<User> friends = userService.getByIdList(friendIds);
			map.put("friends", friends);
		}
		return map;
	}

	@ActionAnnotation(action = "uploadPlayerInfo")
	public Map<String, Object> uploadPlayerInfo(BaseActionDataDto baseData) {
		Map<String, Object> map = Maps.newHashMap();
		BaseActionDataDto.UploadPlayerData data = (BaseActionDataDto.UploadPlayerData) baseData;
		OnlineUserDto onlineUser = GameMemory.getUser();
		User userFromDB = userService.getById(onlineUser.getId());
		validateUploadedData(data, userFromDB);
		User user = data.getExtAttrs().get("self");
		user.setId(onlineUser.getId());
		userService.updateSelectiveById(user);
		return map;
	}

	private void validateUploadedData(UploadPlayerData data, User userFromDB) {
		// TODO Auto-generated method stub
		// TODO validate upload message
		Map<String, User> map = data.getExtAttrs();
		map.get("roomId");
		User user = map.get("self");
	}
	
	
	

}
