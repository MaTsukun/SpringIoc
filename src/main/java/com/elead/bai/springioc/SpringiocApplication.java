package com.elead.bai.springioc;


import com.elead.bai.springioc.controller.UserInfoController;
import com.elead.bai.springioc.core.UdaSpringContext;

public class SpringiocApplication {

	public static void main(String[] args) {
		UdaSpringContext context = new UdaSpringContext();
		try {
			System.out.println("开始初始化容器");
			long t = System.currentTimeMillis();
			context.initContext("com.elead.bai.springioc");
			System.out.println("初始化容器成功，cost time:" + (System.currentTimeMillis() - t));
			UserInfoController controller = context.getBean("userInfoController");
			controller.addUser();
		} catch (Exception e) {
			e.printStackTrace();
		}}

}
