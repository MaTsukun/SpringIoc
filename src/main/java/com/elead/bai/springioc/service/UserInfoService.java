package com.elead.bai.springioc.service;


import com.elead.bai.springioc.annotation.UdaAutowired;
import com.elead.bai.springioc.annotation.UdaComponent;
import com.elead.bai.springioc.dao.UserInfoDao;

@UdaComponent
public class UserInfoService {
	@UdaAutowired
	private UserInfoDao userInfoDao;

	public void addUser() {
		userInfoDao.addUser();
	}

	public String getUser() {
		return userInfoDao.getUser();
	}

	public String getShowInfo(String name, Integer age) {
		return String.format("我叫%s, 今年%s岁。", name, age);
	}
}
