package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.bomb.Dao.UserDao;
import com.game.bomb.domain.User;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;

/**
 * @author CHQ
 * @since 2013-8-1 
 */
@Component
@DomainMetadata(domainClass=User.class, idColumn="id", idProperty="id", tableName="user")
public class UserDaoImpl extends BaseDaoSqlImpl<User> implements UserDao{

}
