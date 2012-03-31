package com.nublic.app.photos.web.client.controller;

public enum View {
	AS_CELLS("cells"),
	AS_PRESENTATION("presentation");
	
	String param;
	
	private View(String param) {
		this.param = param;
	}
	
	public static View fromParameter(String p) {
		for (View v : values()) {
			if (v.param.equals(p)) {
				return v;
			}
		}
		return null;
	}
}
