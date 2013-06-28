package com.wenxiong.blog.dao.sql;

import org.springframework.stereotype.Repository;

import com.wenxiong.blog.User;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;
import com.wenxiong.blog.dao.UserDao;


@DomainMetadata(domainClass = User.class, tableName = "wp_users", idColumn="ID", idProperty="id")
@Repository("userDao")
public class UserDaoImpl extends BaseDaoSqlImpl<User> implements UserDao{

}
