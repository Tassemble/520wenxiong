package com.wenxiong.blog.dao.sql;

import org.springframework.stereotype.Repository;

import com.wenxiong.blog.WPPost;
import com.wenxiong.blog.WPTermRelationship;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;
import com.wenxiong.blog.dao.WPTermRelationshipDao;



@DomainMetadata(domainClass = WPPost.class, tableName = "wp_term_relationships")
@Repository("wpTermRelationshipDao")
public class WPTermRelationshipDaoImpl extends BaseDaoSqlImpl<WPTermRelationship> implements WPTermRelationshipDao {

}
