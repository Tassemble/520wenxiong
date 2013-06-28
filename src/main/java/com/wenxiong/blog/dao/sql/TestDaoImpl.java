package com.wenxiong.blog.dao.sql;

import org.springframework.stereotype.Repository;

import com.wenxiong.blog.Test;
import com.wenxiong.blog.commons.dao.BaseDao;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;
import com.wenxiong.blog.dao.TestDao;



@DomainMetadata(domainClass = Test.class, tableName = "test", policyIdProperty = "id")
@Repository("testDao")
public class TestDaoImpl extends BaseDaoSqlImpl<Test> implements TestDao {

}
