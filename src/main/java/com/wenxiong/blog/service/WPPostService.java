package com.wenxiong.blog.service;

import java.util.Map;

import com.wenxiong.blog.WPPost;
import com.wenxiong.blog.commons.service.BaseService;

public interface WPPostService extends BaseService<WPPost> {

	public String	KEY_USER		= "user";
	public String	KEY_ARTICLE_ID	= "articleId";
	public String	KEY_ORIGIN_URL	= "origin";

	boolean postFeatureFileAndUpdateAttachment(Long postId, String firstPicture);

	Map<String, Object> addOneArticle(String tmallUrl, Long userId);

}
