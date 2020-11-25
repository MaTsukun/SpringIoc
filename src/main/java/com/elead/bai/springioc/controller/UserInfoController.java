package com.elead.bai.springioc.controller;


import com.elead.bai.springioc.annotation.UdaAutowired;
import com.elead.bai.springioc.annotation.UdaComponent;
import com.elead.bai.springioc.service.UserInfoService;

@UdaComponent
public class UserInfoController {

	@UdaAutowired
	private UserInfoService userInfoService;

	public void addUser() {
		userInfoService.addUser();
	}
}
