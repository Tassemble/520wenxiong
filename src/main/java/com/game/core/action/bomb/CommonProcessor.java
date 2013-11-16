package com.game.core.action.bomb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.mina.core.session.IoSession;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.bomb.Dao.TransactionDao;
import com.game.bomb.Dao.UserMeta;
import com.game.bomb.Dao.UserMetaDao;
import com.game.bomb.config.BombConfig;
import com.game.bomb.domain.Transaction;
import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.FriendRelationService;
import com.game.bomb.service.TransactionService;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.action.processor.ActionAnotationProcessor;
import com.game.core.action.processor.PlayerInfoProcessorHelper;
import com.game.core.annotation.ActionAnnotation;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnConstant;
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.MessageNullException;
import com.game.core.exception.NoAuthenticationException;
import com.google.common.collect.Lists;
import com.wenxiong.blog.commons.utils.collection.FieldComparator;
import com.wenxiong.blog.commons.utils.text.JsonUtils;
import com.wenxiong.utils.GsonUtils;
import com.wenxiong.utils.HttpClientUtils;
import com.wenxiong.utils.HttpDataProvider;

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
	
	@Autowired
	RoomLogic roomLogic;

	private static final Logger	LOG			= LoggerFactory.getLogger(CommonProcessor.class);

	
	@Autowired
	BombConfig bombConfig;
	
	
	@Autowired
	HttpClientUtils httpClientUtils;
	
	@Autowired
	TransactionService transactionService;
	
	//验证receipt
	@ActionAnnotation(action = "receiptData")
	public Map<String, Object> verifyReceiptData(Object message, Map<String, Object> map) {
		
		JSONObject jsonRoot = JSONObject.fromObject(message);
		final String data = jsonRoot.getString("data");
		if (StringUtils.isBlank(data)) {
			throw new ActionFailedException("data is empty!!", jsonRoot.getString(action));
		}
		
		if (LOG.isDebugEnabled()) { 
			LOG.info("verify data:" + data);
		}
		
		
		return transactionService.createAfterVerified(data, map);	
	}
	
	//lose
	//runaway
	@ActionAnnotation(action = "runaway")
	public void runaway(Object message, Map<String, Object> map) throws Exception {
		validateUserStatus("runaway");
		
		OnlineUserDto user = GameMemory.getUser();
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		roomLogic.doUserQuit(room, user.getUsername());
		
		return;
	}
	
	@ActionAnnotation(action = "lose")
	public void lose(Object message, Map<String, Object> map) throws Exception {
		validateUserStatus("lose");
		OnlineUserDto user = GameMemory.getUser();
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		roomLogic.doUserQuit(room, user.getUsername());
	}
	
	private void validateUserStatus(String action) {
		OnlineUserDto user = GameMemory.getUser();
		
		if (user == null) {
			throw new NoAuthenticationException(action);
		}
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		
		if (room == null) {
			throw new ActionFailedException(action);
		}
		
		 if (!(OnlineUserDto.STATUS_PLAYING.equals(user.getStatus()) || OnlineUserDto.STATUS_IN_ROOM.equals(user.getStatus()))) {
			 throw new ActionFailedException(action);
		 }
	}
	
	
	
	// ~ 如果是mock 请不要调用改接口
	@SuppressWarnings("unchecked")
	@ActionAnnotation(action = "downloadPlayerInfo")
	public void downloadPlayerInfo(Object message, Map<String, Object> map) throws Exception {
		playerInfoProcessorHelper.innerDownloadPlayerInfo(map);
		IoSession session = GameMemory.getCurrentSession();
		session.write(map);
	}
	
	@ActionAnnotation(action = "getOnlineUserList")
	public Map<String, Object> getOnlineUserList(Object message, Map<String, Object> map) throws Exception {
		// ~ 老代码 需要移植到新的逻辑上去
			List<OnlineUserDto> users = Lists.newArrayList();
			int limit = 100;
			for (Entry<String, OnlineUserDto> entry : GameMemory.onlineUsers.entrySet()) {
				limit--;
				if (limit == 0) {
					break;
				}
				users.add(entry.getValue());
			}
			
			Collections.sort(users, new FieldComparator<OnlineUserDto>("level", false));
			List<MobileUserDto> mUsers = Lists.newArrayList();
			for (OnlineUserDto u : users) {
				MobileUserDto mUser = new MobileUserDto(u);
				mUsers.add(mUser);
			}
			map.put("code", 200);
			map.put("result", mUsers);
			return map;
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
		update.setInUse(json.getString("inUse"));
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
			updateMemberMetaItem(json, UserMeta.USER_INVENTORY_ITEM);
			map.put("code", 200);
			map.put("message", "uploadInventoryItem successfully");
			return map;
		} catch (Exception e) {
			map.put("code", -20);
			map.put("message", "uploadInventoryItem failed:" + message);
			return map;
		}
	}
	
	
	@ActionAnnotation(action = "uploadMedals")
	public Map<String, Object> uploadMedals(Object message, Map<String, Object> map) {
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(message);
			String items = json.getString("medal");
			if (StringUtils.isBlank(items)) {
				map.put("code", -1);
				map.put("message", "update failed, because no items existed:" + message);
				return map;
			}
			json.discard(action);
			updateMemberMetaItem(json, UserMeta.USER_MEDAL);
			map.put("code", 200);
			map.put("message", "uploadMedals successfully");
			return map;
		} catch (Exception e) {
			map.put("code", -20);
			map.put("message", "uploadMedals failed:" + message);
			return map;
		}
	}

	
	@ActionAnnotation(action = "downloadMedals")
	public Map<String, Object> downloadMedals(Object message, Map<String, Object> map) {
		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), UserMeta.USER_MEDAL);
		if (userMeta == null || StringUtils.isBlank(userMeta.getValue())) {
			map.put("code", 200);
			map.put("message", "you have nothing!");
			return map;
		} else {
			JSONObject json = JSONObject.fromObject(userMeta.getValue());
			json.discard(action);
			json.discard(CODE_NAME);
			json.accumulate(action, "downloadMedals");
			json.accumulate(CODE_NAME, 200);
			GameMemory.getCurrentSession().write(json);
			return null;
		}
	}
	
	
	private void updateMemberMetaItem(JSONObject json, String key) {
		String updatedValue = GsonUtils.toJson(json);

		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), key);
		UserMeta newItem = new UserMeta();
		newItem.setKey(key);
		newItem.setValue(updatedValue);
		newItem.setUserId(onlineUser.getId());
		if (userMeta == null) {
			userMetaDao.add(newItem);
		} else {
			userMetaDao.updateSelectiveByCondition(newItem, "user_id = ? and user_key = ?",
					onlineUser.getId(), key);
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
//			HashMap<Object, Object> map = new mapper.readValue(json, HashMap.class);
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
//		String json = "{\"a\":\"b\", \"d\":{\"a\":\"b\"}}";

		long time = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++) {
			
			ObjectMapper mapper = new ObjectMapper();
		}
		System.out.println(System.currentTimeMillis() - time );
//
//		HashMap<Object, Object> map = mapper.readValue(json, HashMap.class);
//		Map o = (Map) map.get("d");
//		System.out.println(o.get("a"));

	}

}
