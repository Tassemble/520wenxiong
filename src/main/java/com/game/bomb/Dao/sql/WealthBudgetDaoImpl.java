package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.bomb.Dao.WealthBudgetDao;
import com.game.bomb.domain.WealthBudget;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;

/**
 * @author CHQ
 * @since 2013-11-16 
 */
@Component
@DomainMetadata(domainClass=WealthBudget.class, idColumn="id", idProperty="id", tableName="wealth_budget")
public class WealthBudgetDaoImpl extends BaseDaoSqlImpl<WealthBudget> implements WealthBudgetDao{

}
