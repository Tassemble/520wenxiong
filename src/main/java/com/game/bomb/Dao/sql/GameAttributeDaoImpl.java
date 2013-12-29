package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.bomb.Dao.GameAttributeDao;
import com.game.bomb.domain.GameAttribute;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;


@Component
@DomainMetadata(domainClass=GameAttribute.class)
public class GameAttributeDaoImpl extends BaseDaoSqlImpl<GameAttribute> implements GameAttributeDao{

}
