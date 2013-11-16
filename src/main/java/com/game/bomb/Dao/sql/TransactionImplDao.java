package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.bomb.Dao.TransactionDao;
import com.game.bomb.domain.Transaction;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;

/**
 * @author CHQ
 * @since 2013-11-16 
 */
@Component
@DomainMetadata(domainClass=Transaction.class, idColumn="id", idProperty="id")
public class TransactionImplDao extends BaseDaoSqlImpl<Transaction> implements TransactionDao{

}
