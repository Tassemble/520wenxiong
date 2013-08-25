package com.game.core.action.processor;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.Dao.UserMeta;
import com.game.bomb.Dao.UserMetaDao;
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
import com.wenxiong.utils.WordPressUtils;

@Component
public class CommonProcessor implements ActionAnotationProcessor {


	final static String action = "action";
	final static String CODE_NAME = "code";
	@Autowired
	UserService				userService;

	@Autowired
	FriendRelationService	friendRelationService;
	
	@Autowired
	UserMetaDao userMetaDao;

	//~ 如果是mock 请不要调用改接口
	@ActionAnnotation(action = "downloadPlayerInfo")
	public void downloadPlayerInfo(Object message, Map<String, Object> map) {

		
		OnlineUserDto onlineUser = GameMemory.getUser();
		if (onlineUser == null) {
			throw new NoAuthenticationException("downloadPlayerInfo");
		}
		User user = userService.getById(onlineUser.getId());
		map.put("code", 200);
		map.put("self", user);
		// ~ put init
		map.put("friends", null);
		// ~ get friends
		List<FriendRelation> relations = friendRelationService.getByCondition("user_id = ? and relation_status = ?",
				onlineUser.getId(), FriendRelation.STATUS_ACCEPTED);
		if (!CollectionUtils.isEmpty(relations)) {
			List<Long> friendIds = PropertyExtractUtils.getByPropertyValue(relations, "FriendId");
			List<User> friends = userService.getByIdList(friendIds);
			map.put("friends", friends);
		}
	}

	@ActionAnnotation(action = "uploadPlayerInfo")
	public Map<String, Object> uploadPlayerInfo(Object message, Map<String, Object> map) {
		BaseActionDataDto baseData = (BaseActionDataDto) WordPressUtils.getFromJson(message.toString(),
				BaseActionDataDto.getClassByAction("downloadPlayerInfo"));
		
		BaseActionDataDto.UploadPlayerData data = (BaseActionDataDto.UploadPlayerData) baseData;
		OnlineUserDto onlineUser = GameMemory.getUser();
		User userFromDB = userService.getById(onlineUser.getId());
		validateUploadedData(data, userFromDB);
		User user = data.getExtAttrs().get("self");
		user.setId(onlineUser.getId());
		userService.updateSelectiveById(user);
		return map;
	}
	
	//uploadHearts 
	@ActionAnnotation(action = "uploadHearts")
	public Map<String, Object> uploadHearts(Object message, Map<String, Object> map) {
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(message);
			json.discard(action);
			
			
			OnlineUserDto onlineUser = GameMemory.getUser();
			UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?", onlineUser.getId(), UserMeta.HEART_NUM);
			UserMeta newItem = new UserMeta();
			newItem.setKey(UserMeta.HEART_NUM);
			newItem.setValue(String.valueOf(json));
			newItem.setUserId(onlineUser.getId());
			if (userMeta == null) {
				userMetaDao.add(newItem);
			} else {
				userMetaDao.updateSelectiveByCondition(newItem, "user_id = ? and user_key = ?", onlineUser.getId(), UserMeta.HEART_NUM);
			}
			map.put("code", 200);
			map.put("message", "uploadHearts successfully");
			return map;
		} catch (Exception e) {
			map.put("code", -1);
			map.put("message", "uploadHearts failed:" + message);
			return map;
		}
	}
	
	@ActionAnnotation(action = "downloadHearts")
	public Map<String, Object> downloadHearts(Object message, Map<String, Object> map) {
		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?", onlineUser.getId(), UserMeta.HEART_NUM);
		if (userMeta == null || StringUtils.isBlank(userMeta.getValue())) {
			map.put("code", 200);
			map.put("message", "you have nothing!");
			return map;
		} else {
			JSONObject json = JSONObject.fromObject(userMeta.getValue());
			json.discard(action);
			json.discard(CODE_NAME);
			json.accumulate(action, "downloadHearts");
			json.accumulate(CODE_NAME, 200);
			GameMemory.getCurrentSession().write(json);
			//return null due to not to write 
			return null;
		}
	}
	

	private void validateUploadedData(UploadPlayerData data, User userFromDB) {
		// TODO Auto-generated method stub
		// TODO validate upload message
		Map<String, User> map = data.getExtAttrs();
		map.get("roomId");
		User user = map.get("self");
	}
	
	
	@ActionAnnotation(action = "uploadInventoryItem")
	public Map<String, Object> uploadInventoryItem(Object message, Map<String, Object> map) {
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(message);
			String items = json.getString("items");
			if (StringUtils.isBlank(items)) {
				map.put("code", -1);
				map.put("message", "update failed, because no items existed:" + message);
				return map;
			}
			json.discard(action);
			String updatedValue = WordPressUtils.toJson(json);
			
			
			OnlineUserDto onlineUser = GameMemory.getUser();
			UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?", onlineUser.getId(), UserMeta.USER_INVENTORY_ITEM);
			UserMeta newItem = new UserMeta();
			newItem.setKey(UserMeta.USER_INVENTORY_ITEM);
			newItem.setValue(updatedValue);
			newItem.setUserId(onlineUser.getId());
			if (userMeta == null) {
				userMetaDao.add(newItem);
			} else {
				userMetaDao.updateSelectiveByCondition(newItem, "user_id = ? and user_key = ?", onlineUser.getId(), UserMeta.USER_INVENTORY_ITEM);
			}
			map.put("code", 200);
			map.put("message", "uploadInventoryItem successfully");
			return map;
		} catch (Exception e) {
			map.put("code", -20);
			map.put("message", "uploadInventoryItem failed:" + message);
			return map;
		}
	}
	
	@ActionAnnotation(action = "downloadInventoryItem")
	public Map<String, Object> downloadInventoryItem(Object message, Map<String, Object> map) {
		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?", onlineUser.getId(), UserMeta.USER_INVENTORY_ITEM);
		if (userMeta == null || StringUtils.isBlank(userMeta.getValue())) {
			map.put("code", 200);
			map.put("message", "you have nothing!");
			return map;
		} else {
			
			JSONObject json = JSONObject.fromObject(userMeta.getValue());
			json.discard(action);
			json.discard(CODE_NAME);
			json.accumulate(action, "downloadInventoryItem");
			json.accumulate(CODE_NAME, 200);
			GameMemory.getCurrentSession().write(json);
			//return null due to not to write 
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		
		JSONObject json = JSONObject.fromObject("{}");
		json.accumulate("hey", "ok");
		json.discard("hey");
		json.accumulate("hey", "哈哈");
		WordPressUtils.printJson(json);
	}
	
	
	
	
	

}

