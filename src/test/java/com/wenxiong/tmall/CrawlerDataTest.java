package com.wenxiong.tmall;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wenxiong.blog.dao.BaseTestCase;
import com.wenxiong.blog.dto.TmallProductDto;
import com.wenxiong.crawl.taobao.TmallCrawler;
import com.wenxiong.utils.HttpClientUtils;

/**
 * @author CHQ
 * @date Apr 23, 2014
 * @since 1.0
 */
public class CrawlerDataTest extends BaseTestCase{
	
	@Autowired
	TmallCrawler tmallCrawler;
	
	
	@Autowired
	HttpClientUtils httpClientUtils;
	
	Gson       disableEscape  = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
	
	@Test
	public void test() {
		
		Map<String, Object> map = tmallCrawler.getKeyValue("http://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.111.DPic8e&id=23973656966&user_id=1028683301&is_b=1&cat_id=50043345&q=%D5%E4%D6%E9&rn=1b5732d9c30ad1867fa854744f422c58");
		System.out.println(disableEscape.toJson(map));
		
	}
	
	
	@Test
	public void addProduct() throws Exception {
		Map<String, Object> map = tmallCrawler.getKeyValue("http://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.69.o3vG0K&id=35732705465&_u=r1601rhe972&areaId=330100&user_id=1778355322&is_b=1&cat_id=50043345&q=%D5%E4%D6%E9&rn=5ae87dd0a4df3eae0e21b62bec5d8a32");
		
		Map<String, Object> productBasic = new HashMap<String, Object>();
		//
		productBasic.put("type_id", "simple");
		productBasic.put("attribute_set_id", "9");//pearl product
		//TODO product name
		productBasic.put("sku", String.valueOf(System.currentTimeMillis()));
		productBasic.put("name", map.get("title"));
		productBasic.put("meta_title", map.get("title"));
		productBasic.put("meta_description", map.get("title"));
//		productBasic.put("url_key", "new-product");
//		productBasic.put("custom_design", "enterprise/default");
//		productBasic.put("page_layout", "one_column");
//		productBasic.put("options_container", "container2");
//		productBasic.put("country_of_manufacture", "China");
//		productBasic.put("msrp_enabled", "msrp_enabled");
//		productBasic.put("msrp_display_actual_price_type", "msrp_display_actual_price_type");
//		productBasic.put("gift_message_available", "gift_message_available");
		
		
		List<TmallProductDto> dtos = (List<TmallProductDto>)map.get(TmallCrawler.KEY_PRODUCTS);
		TmallProductDto product = dtos.get(dtos.size() - 1);
		
		/**
		 * 
		 */
		productBasic.put("price", String.valueOf(product.getPrice()));
		if (product.getPromotionPrice() != null) {
			productBasic.put("special_price", String.valueOf(product.getPromotionPrice()));
		}
		productBasic.put("weight", "0.5");
//		productBasic.put("msrp", "simple");
		productBasic.put("status", 1);
		productBasic.put("visibility", 4);
//		productBasic.put("enable_googlecheckout", "simple");
		productBasic.put("tax_class_id", 0);
		productBasic.put("description", map.get(TmallCrawler.KEY_CONTENT));
		productBasic.put("short_description", map.get(TmallCrawler.KEY_TITLE));
		productBasic.put("meta_keyword", map.get(TmallCrawler.KEY_TITLE));
//		productBasic.put("custom_layout_update", "simple");
//		productBasic.put("special_from_date", "simple");
//		productBasic.put("special_to_date", "simple");
		
//		productBasic.put("news_from_date", "simple");
//		productBasic.put("news_to_date", "simple");
//		productBasic.put("custom_design_from", "simple");
//		productBasic.put("custom_design_to", "simple");
//		
//		productBasic.put("group_price", "simple");
//		productBasic.put("tier_price", "simple");
		
		Map<String, Object> stockData =  new HashMap<String, Object>();
		stockData.put("qty", String.valueOf(RandomUtils.nextInt(100)));
		stockData.put("min_qty", String.valueOf(0));
//		stockData.put("use_config_min_qty", "simple");
//		stockData.put("is_qty_decimal", "simple");
//		stockData.put("backorders", "simple");
//		stockData.put("use_config_backorders", "simple");
//		stockData.put("min_sale_qty", "simple");
//		stockData.put("use_config_min_sale_qty", "simple");
//		stockData.put("max_sale_qty", "simple");
//		stockData.put("use_config_max_sale_qty", "simple");
		stockData.put("is_in_stock", 1);
//		stockData.put("notify_stock_qty", "simple");
//		stockData.put("use_config_notify_stock_qty", "simple");
//		stockData.put("manage_stock", "simple");
//		stockData.put("use_config_manage_stock", "simple");
//		stockData.put("use_config_qty_increments", "simple");
//		stockData.put("qty_increments", "simple");
//		
//		
//		stockData.put("use_config_enable_qty_inc", "simple");
//		stockData.put("enable_qty_increments", "simple");
//		stockData.put("is_decimal_divided", "simple");
		productBasic.put("stock_data", stockData);

		
		

        OAuthConsumer consumer = new DefaultOAuthConsumer(
                "b99007fea36d0b54adb8a3e4d33c0769",
                "0c3d1e79fefb3213cad4f4c861e4b603");

        OAuthProvider provider = new DefaultOAuthProvider(
                "http://114.215.182.17/oauth/initiate",
                "http://114.215.182.17/oauth/token",
                "http://114.215.182.17/chq/oauth_authorize");
        System.out.println("Fetching request token from Twitter...");

        // we do not support callbacks, thus pass OOB
        String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);

        System.out.println("Request token: " + consumer.getToken());
        System.out.println("Token secret: " + consumer.getTokenSecret());
        
        System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
        System.out.println("Enter the PIN code and hit ENTER when you're done:");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String pin = br.readLine();

        System.out.println("Fetching access token from Twitter...");

        provider.retrieveAccessToken(consumer, pin);

        System.out.println("Access token: " + consumer.getToken());
        System.out.println("Token secret: " + consumer.getTokenSecret());

        
        //TODO
//        HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();
//        HttpPost request = new HttpPost("http://114.215.182.17/api/rest/products");
        

        
        
        URL url = new URL("http://114.215.182.17/api/rest/products");
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestMethod("POST");
        request.setRequestProperty("Content-Type", "application/json");
        request.setDoOutput(true);
        request.setDoInput(true);
        request.setRequestProperty("Accept", "application/json");
        
        consumer.sign(request);
        
        
        request.getOutputStream().write(disableEscape.toJson(productBasic).getBytes("UTF-8"));
//        request.connect();
        
        //为什么会读不到 读是读到了，原因是产品没有发布导致读不到
        BufferedInputStream in = new BufferedInputStream(request.getInputStream());  
        //用Stream取得返回的HTML文件  
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();  
        //用OutputStream来接收  
        byte bb[] = new byte[1024];  
        int length = 0;  
        while ((length = in.read(bb, 0, bb.length)) != -1) {  
            byteout.write(bb, 0, length);  
        }  
        //用文本方式来接收  
        String ret = byteout.toString();
        System.out.println(ret);
        in.close();  
        byteout.close();  
        
//        
//        int code = request.getResponseCode();
//        String message = request.getResponseMessage();
//        String content = request.getContent().toString();
//        System.out.println("Response: " + request.getResponseCode() + " "
//                + request.getResponseMessage());
//        System.out.println( "content:" + request.getContent());
//
//		BufferedReader buffer = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
//
//		String line = null;
//		StringBuilder sb = new StringBuilder();
//		while ((line = buffer.readLine()) != null) {
//			sb.append(line + "\n");
//		}
//
//		br.close();
//
//		System.out.println("" + sb.toString());        
//        
	}
	
	
	
	

}
