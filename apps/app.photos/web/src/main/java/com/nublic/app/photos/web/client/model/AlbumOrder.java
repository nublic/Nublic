package com.nublic.app.photos.web.client.model;

public enum AlbumOrder {
	DATE_ASC("date/asc"),
	DATE_DES("date/desc"),
	TITLE_ASC("title/asc"),
	TITLE_DESC("title/desc");
	
	String param;
	
	private AlbumOrder(String param) {
		this.param = param;
	}
	
	public String getParameter() {
		return this.param;
	}
	
	public static AlbumOrder fromParameter(String p) {
		for (AlbumOrder v : values()) {
			if (v.param.equals(p)) {
				return v;
			}
		}
		return null;
	}
}
