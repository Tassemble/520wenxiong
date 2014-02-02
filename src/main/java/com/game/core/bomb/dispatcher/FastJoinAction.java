package com.game.core.bomb.dispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.base.commons.utils.text.JsonUtils;
import com.game.bomb.Dao.MatchPolicyDao;
import com.game.bomb.constant.BombConstant;
import com.game.bomb.domain.MatchPolicy;
import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.MobRoomDto;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.BaseActionDataDto;
import com.game.core.bomb.dto.BaseActionDataDto.FastJoinData;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.bomb.play.dto.FastJoinTimeoutCallback;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.BombException;
import com.game.core.exception.ExceptionConstant;
import com.game.core.exception.GamePlayException;
import com.game.core.utils.CellLocker;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Component
public class FastJoinAction implements BaseAction {

	@Autowired
	CellLocker<List<String>>	locker;

	@Autowired
	RoomLogic					roomLogic;
	
	@Autowired
	MatchPolicyDao matchPolicyDao;
	
	@Autowired
	UserService userService;
	
	
	private static Logger		LOG	= LoggerFactory.getLogger(FastJoinAction.class);

	@Override
	public void doAction(IoSession session, BaseActionDataDto data) throws Exception {
		// check user status
		OnlineUserDto user = GameMemory.SESSION_USERS.get(session.getId());
		checkUserStatus(user);
		
		
		
		User userInDB = userService.getById(user.getId());
		if (userInDB.getHeartNum() == null || userInDB.getHeartNum() <= 0) {
			session.write(new ReturnDto(BombConstant.NOT_ENOUGH_HEART_CODE, data.getAction(), "no heart, you can wait or buy it"));
			return;
		}
		
		//查找一个level
		
		List<MatchPolicy> policies = matchPolicyDao.getByCondition("1=1 order by pk_level asc");
		if (CollectionUtils.isEmpty(policies)) {
			throw new BombException(ExceptionConstant.GAME_START_EXCEPTION, "no policies in db, please configure it");
		}
		
		FastJoinData joinData = (FastJoinData) data;
		int userNumLimit = joinData.getPlayersNum();

		PlayRoomDto room = null;
		Map<String, Object> bestPolicyMap = findBestPolicy(policies, user);
		
		
		Integer bestPosition = 0;
		int policyLevel = MatchPolicy.DEFAULT_POLICY_LEVEL;
		if (!MapUtils.isEmpty(bestPolicyMap)) {
			bestPosition = (Integer)bestPolicyMap.get("position");
			MatchPolicy bestPolicy = (MatchPolicy)bestPolicyMap.get("policy");
			policyLevel = bestPolicy.getPkLevel();
		}
		
		//如果找不到就找相邻的梯级
		for (int i = 0; i < policies.size(); i++) {
			room = matchRoom(policies, userNumLimit, room, bestPosition + i);
			if (room != null) {
				break;
			}
			//优化 因为再一次match room 还是和上一次match room 一样
			if (bestPosition == 0 && i == 0) {
				continue;
			}
			
			room = matchRoom(policies, userNumLimit, room, bestPosition - i);
			if (room != null) {
				break;
			}
		}

		try {
			if (room == null) {// create new room
				// 这里不需要同步，原因是在没有创建好房间的时候，其他用户是看到这个房间的
				room = new PlayRoomDto(userNumLimit, user, policyLevel);
				GameMemory.room.put(room.getId(), room);
				FastJoinTimeoutCallback timeoutTask = new FastJoinTimeoutCallback(user.getId(), joinData.getTimeoutInSeconds());
				user.setTimeoutTask(timeoutTask);
				room.addUserCallback(timeoutTask);
				LOG.info("user[" + user.getUsername() + "] create room, rid:" + room.getId());
			} else {
				roomLogic.doUserJoin(room, user.getId());
				FastJoinTimeoutCallback timeoutTask = new FastJoinTimeoutCallback(user.getId(), joinData.getTimeoutInSeconds());
				user.setTimeoutTask(timeoutTask);
				room.addUserCallback(timeoutTask);
				LOG.info("user[" + user.getUsername() + "] join room, rid:" + room.getId());
			}

			//send back players infos
			if (room.isReadyToStart()) {
				// online users
				List<MobileUserDto> players = Lists.newArrayList();
				List<Long> userIds = Lists.newArrayList();
				
				for (OnlineUserDto oUser : room.getUsers()) {
					userIds.add(oUser.getId());
				}
				
				List<User> users = userService.getByIdList(userIds);
				for (User userFromDB : users) {
					MobileUserDto mUser = MobileUserDto.buildMobileUser(userFromDB);
					players.add(mUser);
				}

				ReturnDto ro = new ReturnDto(200, this.getAction(), "players can play game now, game started!");
				ro.setExtAttrs(ImmutableMap.of("players", players, "room", new MobRoomDto(room)));
				RoomLogic.forwardMessageInRoom(room.getId(), ro);
			}
		} catch (Exception e) {
			//this should not happen
			LOG.error(e.getMessage(), e);
			if (room != null) {
				RoomLogic.destroyRoom(room);
			}

			throw new GamePlayException(ExceptionConstant.JOIN_ROOM_FAILED, "create or join room failed. cause:"
					+ e.getMessage());
		}

	}

	private PlayRoomDto matchRoom(List<MatchPolicy> policies, int userNumLimit, PlayRoomDto room, int i) {
		MatchPolicy policy = findNextPolicy(policies, i);
		if (policy == null) {
			return null;
		}
		for (Entry<String, PlayRoomDto> entry : GameMemory.room.entrySet()) {
			if (isMatchRoom(userNumLimit, entry.getValue(), policy.getPkLevel())) {
				room = entry.getValue();
				break;
			}
		}
		return room;
	}

	private MatchPolicy findNextPolicy(List<MatchPolicy> policies, int i) {
		if (i < 0 || i >= policies.size()) {
			return null;
		}
		
		return policies.get(i);
	}

	private Map<String, Object> findBestPolicy(List<MatchPolicy> policies, OnlineUserDto user) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (CollectionUtils.isEmpty(policies) || user == null) {
			throw new BombException(-1, "find best policy failed, policy is empty or user null, for user:" + JsonUtils.toJson(user));
		}
		
		if (user.getVictoryNum() == null || user.getLoserNum() == null) {
			throw new BombException(-1, "user victory null is null or lose number is null, for user:" + JsonUtils.toJson(user));
		}
		
		
		for (int idx = 0; idx < policies.size(); idx++) {
			MatchPolicy matchPolicy = policies.get(idx);
			if (user.getVictoryNum() >= matchPolicy.getWinLow() && user.getVictoryNum() < matchPolicy.getWinHigh()
					&&  user.getLoserNum() >= matchPolicy.getLoseLow() &&  user.getLoserNum() < matchPolicy.getLoseHigh())
			{
				map.put("position", idx);
				map.put("policy", matchPolicy);
				return map;
			}
		}
		
		return map;
	}

	private boolean isMatchRoom(int userNumLimit, PlayRoomDto room) {
		return room.getReadyNumNow() < room.getRoomNumLimit() && room.getRoomNumLimit() == userNumLimit
				&& PlayRoomDto.ROOM_STATUS_OPEN == room.getRoomStatus();
	}
	
	private boolean isMatchRoom(int userNumLimit, PlayRoomDto room, Integer pkLevel) {
		//增加一个条件，必须是相同的level的用户，才能进行PK
		Integer roomPkLevel = room.getPkLevel();
		if (!pkLevel.equals(roomPkLevel)) {
			return false;
		}
		return room.getReadyNumNow() < room.getRoomNumLimit() && room.getRoomNumLimit() == userNumLimit
				&& PlayRoomDto.ROOM_STATUS_OPEN == room.getRoomStatus();
	}
	
	

	@Override
	public String getAction() {
		return ActionNameEnum.FAST_JOIN.getAction();
	}

	private void checkUserStatus(OnlineUserDto user) {
		//判断是否等待状态
		if (user.getStatus().equals(OnlineUserDto.STATUS_IN_ROOM)) {
			PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
			synchronized (room.getRoomLock()) {
				if (user.getStatus().equals(OnlineUserDto.STATUS_IN_ROOM)) { // double check，中断这个等待
					user.getTimeoutTask().interrupt();
					user.setStatus(OnlineUserDto.STATUS_ONLINE);
					room.removeUser(user);
				} else {
					//may in playing game
					throw new BombException(-100, "user status error, may be you are in playing game!");
				}
			}
		} else if (!user.getStatus().equals(OnlineUserDto.STATUS_ONLINE)) { //用户没有在等待, 需要判断是否是在线，如果不是在线状态则不能fast join
			throw new BombException(-100, "user status error");
		}
	}

}
