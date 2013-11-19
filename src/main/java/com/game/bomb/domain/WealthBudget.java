package com.game.bomb.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import com.wenxiong.blog.commons.domain.BaseDo;

public class WealthBudget extends BaseDo{

	
	public static final Long DEFAULT_WEALTH = 10L;
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8262623129130110008L;

	
	Long uid;
	
	Long quantity;//表示元宝个数
	
	String budgetType;
	
	
	@DataProperty(column="budget_type")
	public String getBudgetType() {
		return budgetType;
	}

	public void setBudgetType(String budgetType) {
		this.budgetType = budgetType;
	}

	Long orderId;
	
	
	public static final String BUDGET_TYPE_PAY = "pay";
	public static final String BUDGET_TYPE_SIGNUP = "signup";
	
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}



	
	@DataProperty(column="order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	
	
	
	
	
}
