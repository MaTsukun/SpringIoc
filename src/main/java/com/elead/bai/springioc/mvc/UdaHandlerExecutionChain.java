package com.elead.bai.springioc.mvc;

import java.lang.reflect.Method;

public class UdaHandlerExecutionChain {

	private Object handler;

	private Method method;

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
