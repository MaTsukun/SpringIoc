package com.elead.bai.springioc.core;

import com.elead.bai.springioc.annotation.UdaAutowired;
import com.elead.bai.springioc.annotation.UdaComponent;
import com.elead.bai.springioc.annotation.UdaController;
import com.elead.bai.springioc.annotation.UdaRequestMapping;
import com.elead.bai.springioc.mvc.UdaHandlerExecutionChain;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

public class UdaSpringContext {

	private UdaBeanFactory beanFactory = new UdaBeanFactory();

	private Map<String, UdaHandlerExecutionChain> handlerMapping;

	private ServletContext servletContext;

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		// 将Spring容器存到servlet容器中
		this.servletContext.setAttribute(UdaSpringContext.class.getName(), this);
	}

	/**
	 * 初始化容器
	 * 
	 * @param path
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void initContext(String path) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		path = path.replace(".", "/");
		ClassLoader classLoader = UdaSpringContext.class.getClassLoader();
		//通过类加载器得到当前path路径
		URL url = classLoader.getResource(path);
		File file = new File(url.getPath());
		List<File> clzList = new ArrayList<File>();
		//递归当前path路径下所有class文件。
		getFileList(file, clzList);
		List<Class<?>> classList = new ArrayList<Class<?>>();
		for (File clz : clzList) {
			// 循环遍历class文件，得到类名。
			String apath = clz.getAbsolutePath();
			apath = apath.replace("\\", "/");
			int pre = apath.indexOf(path);
			String clzName = apath.substring(pre);
			clzName = clzName.replace(".class", "").replace("/", ".");
			// 通过类加载器加载这个类
			Class<?> loadClass = classLoader.loadClass(clzName);
			// 扫描类上面的注解
			UdaComponent annotation = loadClass.getAnnotation(UdaComponent.class);
			if (annotation != null) {
				String name = annotation.value();
				beanFactory.createBean(loadClass, name);
				classList.add(loadClass);
			}
			// 扫描带有UdaController注解的类
			UdaController controllerAnno = loadClass.getAnnotation(UdaController.class);
			if (controllerAnno != null) {
				Object createBean = beanFactory.createBean(loadClass, null);
				// 类路径
				UdaRequestMapping clzMapping = loadClass.getAnnotation(UdaRequestMapping.class);
				String clzUrl = clzMapping == null ? "" : clzMapping.value();
				// 检查所有public的方法，是否含有UdaRequestMapping注解
				checkMethod(loadClass, createBean, clzUrl);
				classList.add(loadClass);
			}
		}
		for (Class<?> loadClass : classList) {
			// 注 入
			injection(loadClass);
		}
	}

	private void checkMethod(Class<?> loadClass, Object handler, String clzUrl) {
		Method[] methods = loadClass.getMethods();
		for (Method method : methods) {
			UdaRequestMapping mapping = method.getAnnotation(UdaRequestMapping.class);
			if (mapping != null) {
				// 方法路径
				String methodUrl = mapping.value();
				// 构造执行链
				UdaHandlerExecutionChain chain = new UdaHandlerExecutionChain();
				chain.setHandler(handler);
				chain.setMethod(method);
				this.addMapping(clzUrl + methodUrl, chain);
			}
		}
	}

	/**
	 * 添加执行链映射
	 * @param url
	 * @param chain
	 */
	private void addMapping(String url, UdaHandlerExecutionChain chain) {
		if (handlerMapping == null) {
			handlerMapping = new HashMap<String, UdaHandlerExecutionChain>();
		}
		handlerMapping.put(url, chain);
	}
	
	/**
	 * 获取执行链
	 * @param url
	 * @return
	 */
	public UdaHandlerExecutionChain getHandlerChain(String url) {
		return handlerMapping.get(url);
	}

	private void injection(Class<?> loadClass) throws IllegalAccessException {
		// 扫描字段上的注解，进行注入
		Field[] fields = loadClass.getDeclaredFields();
		// 获取public变量
		for (Field field : fields) {
			UdaAutowired fieldAnno = field.getAnnotation(UdaAutowired.class);
			if (fieldAnno != null) {
				beanFactory.injectionField(loadClass, field);
			}
		}
	}

	public <T> T getBean(Class<?> clz) {
		return beanFactory.getBean(clz);
	}

	public <T> T getBean(String name) {
		return beanFactory.getBean(name);
	}

	/**
	 * 扫描类
	 * 
	 * @param file
	 * @param clzList
	 */
	private void getFileList(File file, List<File> clzList) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				getFileList(f, clzList);
			}
		} else {
			clzList.add(file);
		}
	}

}
