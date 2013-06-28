package com.wenxiong.blog.dao;

import com.wenxiong.blog.WPPost;
import com.wenxiong.blog.commons.dao.BaseDao;

public interface WPPostDao extends BaseDao<WPPost> {

	void testInsert(String sql);

}
