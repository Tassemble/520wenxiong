package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.bomb.Dao.UserMeta;
import com.game.bomb.Dao.UserMetaDao;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;

@Component
@DomainMetadata(domainClass=UserMeta.class, idColumn="id", idProperty="id", tableName="user_meta")
public class UserMetaDaoImpl extends BaseDaoSqlImpl<UserMeta> implements UserMetaDao{

}
