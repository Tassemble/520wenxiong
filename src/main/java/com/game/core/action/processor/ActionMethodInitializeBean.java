package com.game.core.action.processor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCache;

import com.game.core.annotation.ActionAnnotation;
import com.google.common.collect.Maps;

public class ActionMethodInitializeBean implements InitializingBean, DisposableBean {

	@Autowired
	ListableBeanFactory			listableBeanFactory;

	
	@Autowired
	EhCacheCache ehCacheCache;
	
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//~ 处理request-response的方式 非常简单 使用actionAnotation实现
		Map<String, ActionAnotationProcessor> gameProcessors = listableBeanFactory.getBeansOfType(ActionAnotationProcessor.class);
		if (!MapUtils.isEmpty(gameProcessors)) {
			Collection<ActionAnotationProcessor> processors = gameProcessors.values();
			for (ActionAnotationProcessor actionAnotationProcessor : processors) {
				Method[] meothods = actionAnotationProcessor.getClass().getMethods();
				if (ArrayUtils.isNotEmpty(meothods)) {
					for (Method method : meothods) {
						ActionAnnotation annotation = method.getAnnotation(ActionAnnotation.class);
						Map<String, Object> map = Maps.newHashMap();
						map.put("method", method);
						map.put("object", actionAnotationProcessor);
						ehCacheCache.put(annotation.action(), map);
					}
				}
						
			}
			
		}
	}


}
