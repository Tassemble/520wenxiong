package com.wenxiong.blog.dao.sql;

import org.springframework.stereotype.Repository;

import com.wenxiong.blog.Comment;
import com.wenxiong.blog.commons.dao.annotation.DomainMetadata;
import com.wenxiong.blog.commons.dao.sql.BaseDaoSqlImpl;
import com.wenxiong.blog.dao.CommentDao;


@DomainMetadata(domainClass = Comment.class, tableName = "wp_comments", idColumn="comment_ID", idProperty="commentID")
@Repository("commentDao")
public class CommentDaoImpl extends BaseDaoSqlImpl<Comment> implements CommentDao{

}
