package com.wenxiong.blog.dto;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.wenxiong.blog.Comment;

public class TmallCommentsDto {
	List<Comment>				comments;

	Long						rateCount;
	
	
	

	public static final long	commentLimitCount	= 50;
	
	
	public static final long	commentWordsLength	= 20;

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	
	public boolean isCommentsFull() {
		if (CollectionUtils.isEmpty(comments)) {
			return false;
		}
		if (comments.size() == commentLimitCount)
			return true;
		
		if (comments.size() < commentLimitCount)
			return false;
		return true;
	}

	public Long getRateCount() {
		return rateCount;
	}

	public void setRateCount(Long rateCount) {
		this.rateCount = rateCount;
	}

}
