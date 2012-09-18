package com.nublic.app.photos.common.model;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;

public enum AlbumChangeType {
	ADD(RequestBuilder.PUT),
	REMOVE(RequestBuilder.DELETE);
	
	Method m;
	
	private AlbumChangeType(Method m) {
		this.m = m;
	}
	
	public Method getMethod() {
		return m;
	}
}
