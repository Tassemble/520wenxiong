package com.game.bomb.Dao.sql;

import com.game.bomb.Dao.FriendRelationDao;
import com.game.bomb.domain.FriendRelation;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;



@DomainMetadata(tableName="friend_relation", domainClass=FriendRelation.class, idColumn="id", idProperty="id")
public class FriendRelationDaoImpl extends BaseDaoSqlImpl<FriendRelation> implements FriendRelationDao{

}
