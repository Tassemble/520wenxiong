package com.game.bomb.service;

import java.util.Map;

import com.game.bomb.domain.Transaction;
import com.wenxiong.blog.commons.service.BaseService;

public interface TransactionService extends BaseService<Transaction>{

	Map<String, Object> createAfterVerified(String data, Map<String, Object> map);

}
