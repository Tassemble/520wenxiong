package com.game.core.dto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.game.core.GameMemory;
import com.game.core.MessageSenderHelper;
import com.game.core.bomb.logic.RoomLogic;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxiong.utils.WordPressUtils;


/**
 * @author CHQ
 * @since 2013-7-28
 */
public class RoomDto {

	public static final String			ROOM_STATUS_OPEN	= "open";
	public static final String			ROOM_STATUS_CLOSED	= "closed";

	private static transient AtomicLong	idGenerator			= new AtomicLong(1);
	private transient Object			roomLock			= new Object();
	transient List<OnlineUserDto>		users;
	transient AtomicInteger				cntNow				= new AtomicInteger(0);
	final static transient Logger		LOG					= LoggerFactory.getLogger(RoomDto.class);
	transient ExecutorService			executorService;

	String								id;
	private Integer						playersNum;
	volatile String						roomStatus;

	public RoomDto(int playersNum) {
		executorService = Executors.newFixedThreadPool(playersNum);
		this.playersNum = playersNum;
	}

	
	
	public void setCntNow(AtomicInteger cntNow) {
		this.cntNow = cntNow;
	}



	public Object getRoomLock() {
		return roomLock;
	}



	public void setRoomLock(Object roomLock) {
		this.roomLock = roomLock;
	}



	public void addUserCallback(Runnable task) {
		executorService.submit(task);
	}

	public String getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(String roomStatus) {
		this.roomStatus = roomStatus;
	}

	public List<OnlineUserDto> getUsers() {
		return users;
	}

	public int getCntNow() {
		return cntNow.get();
	}

	public void setUsers(List<OnlineUserDto> users) {
		this.users = users;
	}

	public void increaseCnt() {
		if (cntNow.get() < playersNum) {
			cntNow.addAndGet(1);
		} else {
			throw new RuntimeException("enter room failed");
		}
	}

	public void decreaseCnt() {
		synchronized (roomLock) {
			if (cntNow.get() >= 1) {
				cntNow.addAndGet(-1);
			}
			if (cntNow.get() < 0) {
				cntNow.set(0);
			}
		}
	}

	public Integer getPlayersNum() {
		return playersNum;
	}

	public void setPlayersNum(Integer playersNum) {
		this.playersNum = playersNum;
	}

	public String getRoomId() {
		return id;
	}

	public static String generateRoomId() {
		return String.valueOf(idGenerator.getAndAdd(1L));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isFull() {
		return cntNow.get() == playersNum;
	}

	public boolean isReadyToStart() {
		return cntNow.get() == playersNum;
	}

	public boolean isEmpty() {
		return cntNow.get() == 0;
	}

//	public void doUserQuit(String username) {
//		OnlineUserDto user = GameMemory.getUserByUsername(username);
//		if (!this.id.equals(user.getRoomId())) {// not in this room
//			return;
//		}
//		IoSession session = GameMemory.getSessionByUsername(username);
//		ReturnDto ro = new ReturnDto(200, ActionNameEnum.QUIT_GAME.getAction(), ActionNameEnum.QUIT_GAME.getAction());
//		ro.setExtAttrs(ImmutableMap.of("user", user));
//
//		MessageSenderHelper.forwardMessageToOtherClientsInRoom(session, user, WordPressUtils.toJson(ro));
//		
//		boolean isLastPlayer = false;
//		synchronized (roomLock) {
//			//如果是最后一个玩家
//			if (cntNow.get() == 1) {
//				isLastPlayer = true;
//			}
//			user.setRoomId("");
//			user.setStatus(OnlineUserDto.STATUS_ONLINE);
//			getUsers().remove(user);
//			decreaseCnt();
//			if (isEmpty()) {
//				setRoomStatus(RoomDto.ROOM_STATUS_OPEN);
//			}
//		}
//		
//		if (isLastPlayer) {
//			Map<String, Object> maps = Maps.newHashMap();
//			maps.put("action", "win");
//			maps.put("user", user);
//			maps.put("code", 200);
//			session.write(WordPressUtils.toJson(maps));
//			//UPDATE DB can't autowired
//		}
//	}


	public static class TimeoutCallback implements Runnable {

		int		timeoutInSeconds	= 0;
		String	userId;
		RoomLogic roomLogic;
		
		




		public TimeoutCallback(String userId, int timeoutInSeconds) {
			super();
			this.userId = userId;
			this.timeoutInSeconds = timeoutInSeconds;
			ApplicationContext cxt = (ApplicationContext)GameMemory.bizContext.get(GameMemory.CONTEXT_NAME);
			roomLogic = cxt.getBean(RoomLogic.class);
		}

		
		@Override
		public void run() {
			
			
			
			try {
				TimeUnit.SECONDS.sleep(timeoutInSeconds);
				OnlineUserDto user = GameMemory.getUserByUsername(this.userId);
				if (user == null) {
					return;
				}
				RoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
				if (room == null) {
					return;
				}
				if (ROOM_STATUS_OPEN.equals(room.getRoomStatus())) {
					roomLogic.doUserQuit(room, this.userId);
					IoSession session = GameMemory.getSessionByUsername(this.userId);
					session.write(WordPressUtils.toJson(new ReturnDto(-20, ActionNameEnum.FAST_JOIN.getAction(), "fast-join timeout")));
				} else {
				}
				// LOG.info("game is started");
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	
	

}
