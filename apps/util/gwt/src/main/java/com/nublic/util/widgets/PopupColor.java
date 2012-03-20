package com.nublic.util.widgets;

public enum PopupColor {
	SUCCESS("btn-success"),
	PRIMARY("btn-primary"),
	DANGER("btn-danger"),
	INFO("btn-info");
	
	String cssName;
	
	private PopupColor(String cssName) {
		this.cssName = cssName;
	}
	
	public String getCssName() {
		return cssName;
	}
}
