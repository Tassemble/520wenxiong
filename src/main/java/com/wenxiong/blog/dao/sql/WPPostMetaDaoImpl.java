package com.wenxiong.blog.dao.sql;

import org.springframework.stereotype.Repository;

import com.wenxiong.blog.WPPost;
import com.wenxiong.blog.WPPostMeta;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;
import com.wenxiong.blog.dao.WPPostMetaDao;



@DomainMetadata(idColumn="meta_id", domainClass = WPPostMeta.class, tableName = "wp_postmeta", idProperty = "metaId")
@Repository("wpPostMetaDao")
public class WPPostMetaDaoImpl extends BaseDaoSqlImpl<WPPostMeta> implements WPPostMetaDao {

}
