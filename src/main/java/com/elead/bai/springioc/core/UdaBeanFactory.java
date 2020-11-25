package com.elead.bai.springioc.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UdaBeanFactory {
	private Map<String, Object> beanMap = new HashMap<String, Object>();

	/**
	 * 创建对象
	 * @param clz
	 * @param name
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Object createBean(Class<?> clz, String name) throws InstantiationException, IllegalAccessException {
		Object instance = clz.newInstance();
		if (name == null || name.length() == 0) {
			beanMap.put(lowerCase(clz.getSimpleName()), instance);
		} else {
			beanMap.put(name, instance);
		}
		beanMap.put(clz.getName(), instance);
		return instance;
	}

	private String lowerCase(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<?> clz){
		return (T) beanMap.get(clz.getName());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(String name){
		return (T) beanMap.get(name);
	}

	/**
	 * 注入对象
	 * @param clz
	 * @param field
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void injectionField(Class<?> clz, Field field) throws IllegalArgumentException, IllegalAccessException {
		Class<?> type = field.getType();
		Object curObj = beanMap.get(clz.getName());
		Object injectObj = beanMap.get(type.getName());
		//设置忽略权限
		field.setAccessible(true);
		field.set(curObj, injectObj);
	}

}
