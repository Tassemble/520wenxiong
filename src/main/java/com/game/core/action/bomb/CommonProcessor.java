package com.game.core.action.bomb;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.Dao.UserMeta;
import com.game.bomb.Dao.UserMetaDao;
import com.game.bomb.domain.FriendRelation;
import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.FriendRelationService;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.action.processor.ActionAnotationProcessor;
import com.game.core.action.processor.PlayerInfoProcessorHelper;
import com.game.core.annotation.ActionAnnotation;
import com.game.core.dto.OnlineUserDto;
import com.game.core.dto.ReturnConstant;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.ExceptionConstant;
import com.game.core.exception.MessageNullException;
import com.game.core.exception.NoAuthenticationException;
import com.google.common.collect.Lists;
import com.wenxiong.blog.commons.utils.collection.PropertyExtractUtils;
import com.wenxiong.utils.GsonUtils;

@Component
public class CommonProcessor implements ActionAnotationProcessor {

	final static String			action		= "action";
	final static String			CODE_NAME	= "code";
	@Autowired
	UserService					userService;

	@Autowired
	FriendRelationService		friendRelationService;

	@Autowired
	UserMetaDao					userMetaDao;

	@Autowired
	PlayerInfoProcessorHelper	playerInfoProcessorHelper;

	private static final Logger	LOG			= LoggerFactory.getLogger(CommonProcessor.class);

	// ~ 如果是mock 请不要调用改接口
	@SuppressWarnings("unchecked")
	@ActionAnnotation(action = "downloadPlayerInfo")
	public void downloadPlayerInfo(Object message, Map<String, Object> map) throws Exception {
		playerInfoProcessorHelper.innerDownloadPlayerInfo(map);
		IoSession session = GameMemory.getCurrentSession();
		session.write(map);
	}

	@ActionAnnotation(action = "uploadPlayerInfo")
	public String uploadPlayerInfo(Object message, Map<String, Object> map) {
		String jsonString = (String) message;

		if (StringUtils.isBlank(jsonString)) {
			throw new MessageNullException("user upload no message, uid:" + GameMemory.getUser().getId());
		}

		JSONObject jsonRoot = JSONObject.fromObject(jsonString);
		JSONObject json = jsonRoot.getJSONObject("playerInfo");

		if (json == null) {
			throw new MessageNullException("user upload no message, uid:" + GameMemory.getUser().getId());
		}

		OnlineUserDto onlineUser = GameMemory.getUser();
		User update = new User();

		String nickName = json.getString("nickname");
//		if (!StringUtils.isBlank(nickName)) {
//			if (isNickNameExist(nickName)) {
//				throw new ActionFailedException(ExceptionConstant.NICKNAME_EXIST_CODE, "nickname exist",
//						onlineUser.getAction());
//			}
//		}
		update.setNickName(nickName);
		update.setGmtModified(new Date());
		update.setInUse(json.getString("in_use"));
		update.setPortrait(getValue(json.get("portrait")));
		update.setId(onlineUser.getId());
		userService.updateSelectiveById(update);
		return ReturnConstant.OK;
	}

	public Integer getValue(Object o) {
		if (o == null)
			return null;
		return Integer.valueOf(o.toString());
	}

	public boolean isNickNameExist(String nickName) {
		User query = new User();
		query.setNickName(nickName);
		List<User> us = userService.getByDomainObjectSelective(query);

		if (!CollectionUtils.isEmpty(us)) {
			return true;
		}

		return false;
	}

	// uploadHearts
	@ActionAnnotation(action = "uploadHearts")
	public Map<String, Object> uploadHearts(Object message, Map<String, Object> map) {
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(message);
			json.discard(action);

			OnlineUserDto onlineUser = GameMemory.getUser();
			UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
					onlineUser.getId(), UserMeta.HEART_NUM);
			UserMeta newItem = new UserMeta();
			newItem.setKey(UserMeta.HEART_NUM);
			newItem.setValue(String.valueOf(json));
			newItem.setUserId(onlineUser.getId());
			if (userMeta == null) {
				userMetaDao.add(newItem);
			} else {
				userMetaDao.updateSelectiveByCondition(newItem, "user_id = ? and user_key = ?",
						onlineUser.getId(), UserMeta.HEART_NUM);
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
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), UserMeta.HEART_NUM);
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
			// return null due to not to write
			return null;
		}
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
			String updatedValue = GsonUtils.toJson(json);

			OnlineUserDto onlineUser = GameMemory.getUser();
			UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
					onlineUser.getId(), UserMeta.USER_INVENTORY_ITEM);
			UserMeta newItem = new UserMeta();
			newItem.setKey(UserMeta.USER_INVENTORY_ITEM);
			newItem.setValue(updatedValue);
			newItem.setUserId(onlineUser.getId());
			if (userMeta == null) {
				userMetaDao.add(newItem);
			} else {
				userMetaDao.updateSelectiveByCondition(newItem, "user_id = ? and user_key = ?",
						onlineUser.getId(), UserMeta.USER_INVENTORY_ITEM);
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
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), UserMeta.USER_INVENTORY_ITEM);
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
			// return null due to not to write
			return null;
		}
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		// JSONObject json = JSONObject.fromObject("{}");
		// json.accumulate("hey", "ok");
		// json.discard("hey");
		// json.accumulate("hey", "哈哈");
		// WordPressUtils.printJson(json);
		String json = "{\"a\":\"b\", \"d\":{\"a\":\"b\"}}";

		ObjectMapper mapper = new ObjectMapper();

		HashMap<Object, Object> map = mapper.readValue(json, HashMap.class);
		Map o = (Map) map.get("d");
		System.out.println(o.get("a"));

	}

}
