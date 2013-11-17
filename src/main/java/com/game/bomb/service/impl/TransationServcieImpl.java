package com.game.bomb.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.game.bomb.Dao.TransactionDao;
import com.game.bomb.config.BombConfig;
import com.game.bomb.domain.Transaction;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.TransactionService;
import com.game.core.GameMemory;
import com.game.core.exception.ActionFailedException;
import com.wenxiong.blog.commons.dao.BaseDao;
import com.wenxiong.blog.commons.service.impl.BaseServiceImpl;
import com.wenxiong.blog.commons.utils.text.JsonUtils;
import com.wenxiong.utils.GsonUtils;
import com.wenxiong.utils.HttpClientUtils;
import com.wenxiong.utils.HttpDataProvider;

@Component
public class TransationServcieImpl extends BaseServiceImpl<BaseDao<Transaction>, Transaction> implements TransactionService{

	
	@Autowired
	BombConfig bombConfig;
	
	TransactionDao transactionDao;
	
	
	@Autowired
	HttpClientUtils httpClientUtils;
	

	private  Logger LOG = LoggerFactory.getLogger(TransationServcieImpl.class);




	public TransactionDao getTransactionDao() {
		return transactionDao;
	}







	@Autowired
	public void setTransactionDao(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
		super.setBaseDao(transactionDao);
	}






	@Override
	public Map<String, Object> createAfterVerified(final String data, Map<String, Object> map) {
		MobileUserDto user = null;
		try {
			user = new MobileUserDto(GameMemory.getUser());
			String responseData = HttpClientUtils.getDefaultHtmlByPostMethod(httpClientUtils.getVerifyReceiptDataHttpManager(), new HttpDataProvider() {
				
				@Override
				public String getUrl() {
					return bombConfig.getVerifyReceiptUrl();
				}
				
				@Override
				public HttpEntity getHttpEntity() {
					try {
						Map<String, String> map = new HashMap<String, String>();
						map.put("receipt-data", data);
						LOG.info("send:" + GsonUtils.toJson(map));
						return new StringEntity(GsonUtils.toJson(map), ContentType.APPLICATION_JSON);
					} catch (Exception e) {
						return null;
					}
				}
				
				@Override
				public List<Header> getHeaders() {
					return null;
				}
			});
			
			
			if (StringUtils.isBlank(responseData)) {
				LOG.error("verify failed, apple server response nothing, buyer " + JsonUtils.toJson(GameMemory.getUser()));
			}
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> dataFromAppleMapping = mapper.readValue(responseData, HashMap.class);
			Integer status = (Integer)dataFromAppleMapping.get("status");
			if (status == null || !status.equals(0)) {
				map.put("code", -1);
				map.put("message", "verify failed, see verify-result for more details");
			} else {
				map.put("code", 200);
				Map<String, Object> receiptMapping = (HashMap<String, Object>)dataFromAppleMapping.get("receipt");
				Date now = new Date();
				Transaction query = new Transaction();
				query.setUid(GameMemory.getUser().getId());
				query.setTransactionId((String)receiptMapping.get("transaction_id"));
				List<Transaction> results = this.getByDomainObjectSelective(query);
				if (CollectionUtils.isEmpty(results)) {
					Transaction transaction = new Transaction();
					transaction.setGmtCreate(now);
					transaction.setGmtModified(now);
					transaction.setProductId((String)receiptMapping.get("product_id"));
					transaction.setPurchaseDateMs(Long.valueOf((String)receiptMapping.get("purchase_date_ms")));
					transaction.setQuantity(Integer.valueOf((String)receiptMapping.get("quantity")));
					transaction.setTransactionId((String)receiptMapping.get("transaction_id"));
					transaction.setUid(GameMemory.getUser().getId());
					transaction.setUniqueIdentifier((String)receiptMapping.get("unique_identifier"));
					add(transaction);
				} else {
					Transaction update = new Transaction();
					update.setId(results.get(0).getId());
					update.setGmtModified(new Date());
					update.setProductId((String)receiptMapping.get("product_id"));
					update.setPurchaseDateMs(Long.valueOf((String)receiptMapping.get("purchase_date_ms")));
					update.setQuantity(Integer.valueOf((String)receiptMapping.get("quantity")));
					update.setTransactionId((String)receiptMapping.get("transaction_id"));
					update.setUniqueIdentifier((String)receiptMapping.get("unique_identifier"));
					this.updateSelectiveById(update);
				}
				if (bombConfig.isDebug()) {
					map.put("message", "verify successful");
				}
			}
			
			if (bombConfig.isDebug()) {
				map.put("verify-result-for-debug", dataFromAppleMapping);
			}
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " buyer " + GsonUtils.toJson(user), e);
			throw new ActionFailedException("receipt 交易失败");
		}
	}
	
}
