package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.bomb.Dao.MatchPolicyDao;
import com.game.bomb.domain.MatchPolicy;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;


@Component
@DomainMetadata(domainClass=MatchPolicy.class)
public class MatchPolicyDaoImpl extends BaseDaoSqlImpl<MatchPolicy> implements MatchPolicyDao{

}
