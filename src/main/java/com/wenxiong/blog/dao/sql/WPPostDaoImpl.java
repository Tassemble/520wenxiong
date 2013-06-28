package com.wenxiong.blog.dao.sql;

import org.springframework.stereotype.Repository;

import com.wenxiong.blog.WPPost;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;
import com.wenxiong.blog.dao.WPPostDao;



@DomainMetadata(domainClass = WPPost.class, tableName = "wp_posts", policyIdProperty = "id")
@Repository("wPPostDao")
public class WPPostDaoImpl extends BaseDaoSqlImpl<WPPost> implements WPPostDao {

	
	@Override
	public void testInsert(String sql){
		this.getSqlManager().updateRecord(sql);
	}
}
