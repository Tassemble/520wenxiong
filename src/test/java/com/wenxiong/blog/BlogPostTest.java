package com.wenxiong.blog;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.google.common.collect.ImmutableMap;
import com.wenxiong.blog.dao.BaseTestCase;
import com.wenxiong.blog.dao.CommentDao;
import com.wenxiong.blog.dao.UserDao;
import com.wenxiong.blog.dao.WPPostDao;
import com.wenxiong.blog.dto.TmallCommentsDto;
import com.wenxiong.blog.service.WPPostService;
import com.wenxiong.crawl.taobao.TmallCrawler;
import com.wenxiong.utils.WordPressUtils;

public class BlogPostTest extends BaseTestCase {

	private static final Logger	LOG	= Logger.getLogger(BlogPostTest.class);

	@Autowired
	WPPostDao					blogPostDao;

	@Autowired
	WPPostService postService;
	
	
	@Test
	public void test() throws Exception {
		postService.add(new WPPost());
//		WPPost post = blogPostDao.getFirstOneByCondition("id = ?", 148);
//		//
//		post.setId(165L);
//		post.setGuid("http://520wenxiong.com/?p=" + post.getId());
//		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//		post.setPostDate(timestamp);
//		post.setPostDateGmt(timestamp);
//		post.setPostModified(timestamp);
//		post.setPostModifiedGmt(timestamp);
//		post.setPostTitle("【四周年店庆 98元】黛安芬(Triumph)外贸热销新款聚拢文胸深V性感U型美背品牌内衣文胸单件");
//		post.setPostContent("<div id=\"description\">    	    <div class=\"content\" id=\"J_DivItemDesc\"><p><img align=\"absMiddle\" src=\"http://img03.taobaocdn.com/imgextra/i3/143012796/T2u1bfXm4XXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img align=\"absMiddle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2T5TfXmFXXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p style=\"text-align: center;\"><span style=\"color: #0000ff;\"><strong><span style=\"font-size: 18.0px;\">黛安芬支持七天无理由退换</span></strong></span></p><p style=\"text-align: center;\"><span style=\"color: #0000ff;\"><span style=\"font-size: 18.0px;\">文胸是常规标准尺码，因每个人体型/穿戴有差异，建议按平常穿戴标准选择，极少数客户反应偏小，如平时穿薄杯，全罩杯的建议加大一个罩杯，平时穿厚杯或者中厚杯的建议按正常选择<span style=\"background-color: #f4cccc;font-family: 黑体;background-origin: initial;background-clip: initial;\"><span style=\"background-color: #ffffff;\"></span></span></span></span></p><p style=\"text-align: center;\"><span style=\"font-size: 18.0px;\"><span style=\"background-color: #f4cccc;font-family: 黑体;color: #444444;background-origin: initial;background-clip: initial;\"><span style=\"background-color: #ffffff;\">（</span><span style=\"font-family: 黑体;\"><span style=\"background-color: #ffffff;\">不要随意给中差评！介意勿拍！）</span></span></span></span></p><p><img src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2uEGZXgRaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2fYivXaRcXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p>&nbsp;</p><p><img align=\"middle\" src=\"http://img03.taobaocdn.com/imgextra/i3/143012796/T2E12DXXVXXXXXXXXX_!!143012796.gif\" data-pinit=\"registered\"><img align=\"middle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2hfrCXiJaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img align=\"middle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2vUDCXhRXXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p><img align=\"absMiddle\" src=\"http://img02.taobaocdn.com/imgextra/i2/143012796/T2gjLLXh8aXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p><img align=\"middle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2rJPCXgpaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img align=\"middle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2ZL_CXhRaXXXXXXXX_!!143012796.jpg\"></p><p><img align=\"absMiddle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2wzrMXcJXXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p><img align=\"absMiddle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2L7YKXjFaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p>&nbsp;</p><p><img align=\"middle\" src=\"http://img02.taobaocdn.com/imgextra/i2/143012796/T2DMe8XcFaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p>&nbsp;</p><p><img align=\"absMiddle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2eqe9Xi8XXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><div style=\"visibility: hidden;top: 454.0px;left: 150.0px;\">&nbsp;</div></div></div>");
		// http://gd1.alicdn.com/bao/uploaded/i3/12796020211287767/T1mXoxXaJXXXXXXXXX_!!0-item_pic.jpg_310x310.jpg_.webp
		// LOG.info(gson.toJson(post));
		// post.setPostContent(mysql_real_escape_string(post.getPostContent()));
		// System.out.println(mysql_real_escape_string(post.getPostContent()));
		// blogPostDao.updateSelectiveById(post);
	}

	@Autowired
	WPPostDao	dao;

	@Test
	@Rollback
	public void print() {
		System.out.println("\"" + ", " + "\\\"");
		Long id = dao.getId();
		System.out.println("insert id:" + (id + 1));
		dao.testInsert("insert into IDGenerator (ID) values ( " + (id + 1) + " )");
	}

	@Autowired
	WPPostService	w;
	
	
	@Test
	public void addOnePicture() {
		w.postFeatureFileAndUpdateAttachment(1000484L, "http://img04.taobaocdn.com/bao/uploaded/i4/12768032654388004/T1_7EMXXxcXXXXXXXX_!!0-item_pic.jpg");
	}

	@Test
	public void addFromTmall() {
		// w.postFeatureFileAndUpdateAttachment(1000275L,
		// "http://img03.taobaocdn.com/bao/uploaded/i3/T11f_pXjBwXXaVAMA0_035327.jpg");
		// w.postFeatureFileAndUpdateAttachment(1000201L,
		// "http://img02.taobaocdn.com/imgextra/i2/1023043723/T2b2AcXd4aXXXXXXXX_!!1023043723.jpg");
		List<String> list = new ArrayList<String>();
		// list.add("httplist.add("http://detail.tmall.com/item.htm?id=10809674717");
//		list.add("http://detail.tmall.com/item.htm?id=23276317000");
		//http://detail.tmall.com/item.htm?id=12477082411
		//较特殊
		list.add("http://detail.tmall.com/item.htm?id=10903889586");
		list.add("http://detail.tmall.com/item.htm?id=12201055754");
		list.add("http://detail.tmall.com/item.htm?id=12231501071");
		list.add("http://detail.tmall.com/item.htm?id=12640352127");
		list.add("http://detail.tmall.com/item.htm?id=13797324741");
		list.add("http://detail.tmall.com/item.htm?id=13906081482");
		list.add("http://detail.tmall.com/item.htm?id=14145897987");
		list.add("http://detail.tmall.com/item.htm?id=14164669330");
		list.add("http://detail.tmall.com/item.htm?id=14282243189");
		list.add("http://detail.tmall.com/item.htm?id=14521915637");
		list.add("http://detail.tmall.com/item.htm?id=15107828722");
		list.add("http://detail.tmall.com/item.htm?id=15119634776");
		list.add("http://detail.tmall.com/item.htm?id=15472112055");
		list.add("http://detail.tmall.com/item.htm?id=15589327206");
		list.add("http://detail.tmall.com/item.htm?id=15634978328");
		list.add("http://detail.tmall.com/item.htm?id=15716817987");
		list.add("http://detail.tmall.com/item.htm?id=16066179416");
		list.add("http://detail.tmall.com/item.htm?id=16112061070");
		list.add("http://detail.tmall.com/item.htm?id=16180742282");
		list.add("http://detail.tmall.com/item.htm?id=16284140557");
		list.add("http://detail.tmall.com/item.htm?id=17076028666");
		list.add("http://detail.tmall.com/item.htm?id=17100778232");
		list.add("http://detail.tmall.com/item.htm?id=17308390773");
		list.add("http://detail.tmall.com/item.htm?id=17349653500");
		list.add("http://detail.tmall.com/item.htm?id=17390733367");
		list.add("http://detail.tmall.com/item.htm?id=17391825213");
		list.add("http://detail.tmall.com/item.htm?id=17495988392");
		list.add("http://detail.tmall.com/item.htm?id=17568242789");
		list.add("http://detail.tmall.com/item.htm?id=17568465508");
		list.add("http://detail.tmall.com/item.htm?id=17926675800");
		list.add("http://detail.tmall.com/item.htm?id=18197572902");
		list.add("http://detail.tmall.com/item.htm?id=18218113370");
		list.add("http://detail.tmall.com/item.htm?id=18265654079");
		list.add("http://detail.tmall.com/item.htm?id=18358740171");
		list.add("http://detail.tmall.com/item.htm?id=18428062779");
		list.add("http://detail.tmall.com/item.htm?id=18476375245");
		list.add("http://detail.tmall.com/item.htm?id=18498652004");
		list.add("http://detail.tmall.com/item.htm?id=18993052858");
		list.add("http://detail.tmall.com/item.htm?id=19202076430");
		list.add("http://detail.tmall.com/item.htm?id=19408911422");
		list.add("http://detail.tmall.com/item.htm?id=19630967681");
		list.add("http://detail.tmall.com/item.htm?id=20268859304");
		list.add("http://detail.tmall.com/item.htm?id=20286436806");
		list.add("http://detail.tmall.com/item.htm?id=20503452072");
		list.add("http://detail.tmall.com/item.htm?id=20626276623");
		list.add("http://detail.tmall.com/item.htm?id=21056320229");
		list.add("http://detail.tmall.com/item.htm?id=24030332564");
		list.add("http://detail.tmall.com/item.htm?id=24527884302");
		list.add("http://detail.tmall.com/item.htm?id=24579428608");
		list.add("http://detail.tmall.com/item.htm?id=25478052228");
		list.add("http://detail.tmall.com/item.htm?id=25610264060");
		list.add("http://detail.tmall.com/item.htm?id=4448491963");
		list.add("http://detail.tmall.com/item.htm?id=4600800900");
		list.add("http://detail.tmall.com/item.htm?id=5930224305");
		list.add("http://detail.tmall.com/item.htm?id=6148263287");
		list.add("http://detail.tmall.com/item.htm?id=7700593110");
		list.add("http://detail.tmall.com/item.htm?id=8217086571");
		list.add("http://detail.tmall.com/item.htm?id=8852034747");
		list.add("http://detail.tmall.com/item.htm?id=9833220752");
			

		List<User> users = userDao.getAll();

		for (String url : list) {
			try {
				User user = null;
				while (true) {
					user = users.get(RandomUtils.nextInt(users.size()));
					if (user.getId().equals(1L) || user.getId().equals(2L)) {
						continue;
					}
					break;
				}
				Map<String, Object> map = w.addOneArticle(url, user.getId());
				if (map != null && map.get("exist") != null) {
					boolean exist = (Boolean)map.get("exist");
					if (!exist) {
						w.postFeatureFileAndUpdateAttachment((Long)map.get(WPPostService.KEY_ARTICLE_ID),(String) map.get("first_picture"));
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Test
	public void testNextInt() {
		List<User> users = userDao.getAll();
		User user = null;
		while (true) {
			user = users.get(RandomUtils.nextInt(users.size()));
			if (user.getId().equals(1L) || user.getId().equals(2L)) {
				continue;
			}
			System.out.println(user.getId());
			break;
		}
	}

	@Autowired
	TmallCrawler	tmallCrawler;

	@Autowired
	CommentDao		commentDao;

	@Autowired
	WPPostDao		wpPostDao;

	@Autowired
	UserDao			userDao;

	@Test
	public void testAddUsers() {
		Map<String, String> map = ImmutableMap.of("alf", "奥力芙", "zmzly", "致命朱丽叶", "xier", "喜儿", "dzsasys", "当左手爱上右手",
				"xiyao", "细腰");
		for (Entry<String, String> entry : map.entrySet()) {
			User user = new User();
			user.setId(userDao.getId());
			user.setDisplayName(entry.getValue());
			user.setUserEmail("wx520_wx520@163.com");
			user.setUserLogin(entry.getKey());
			user.setUserNicename(entry.getValue());
			user.setUserPass(DigestUtils.md5Hex("wx520__wx520"));
			user.setUserStatus(0);
			user.setUserRegister(new Timestamp(System.currentTimeMillis()));
			userDao.add(user);
		}

	}

	@Test
	public void getComments() {

		TmallCommentsDto commentsDto = tmallCrawler.getComments("http://detail.tmall.com/item.htm?id=12738735995");
		// WordPressUtils.printJson(commentsDto.getComments());

		for (Comment c : commentsDto.getComments()) {
			c.setCommentID(commentDao.getId());
			c.setCommentPostID(1000265L);
			c.setUserId(0L);
			commentDao.add(c);
		}

		long cnt = commentDao.countByCondition("comment_post_ID = ?", 1000265L);
		WPPost changedValue = new WPPost();
		changedValue.setCommentCount(cnt);
		wpPostDao.updateSelectiveByCondition(changedValue, "ID = ?", 1000265L);
	}

	@Test
	public void getScore() {
		Map<String, Object> map = tmallCrawler
				.getProductEvaluation("http://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.176.tqPHGJ&id=9511696318&user_id=655788246&is_b=1&cat_id=50095659&q=%CE%C4%D0%D8&rn=520c73b221605b192168f24d4fc6a3f7");
		for (Entry<String, Object> entry : map.entrySet()) {
			System.err.println(entry.getKey() + ", " + entry.getValue());
		}
	}
	
	@Test
	public void getPrice() {
		WordPressUtils.printJson(tmallCrawler.getTmallProductDto("http://detail.tmall.com/item.htm?id=12738735995"));
	}

}
