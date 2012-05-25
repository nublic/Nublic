package com.nublic.app.market.web.client.model;

//<package_status> ::= "installed"
//| "installing"
//| "not-installed"
//| "error" (was tried to install and did not success)
public enum AppStatus {
	INSTALLED("installed"),
	INSTALLING("installing"),
	NOT_INSTALLED("not-installed"),
	ERROR("error");
	
	String statusStr;
	
	AppStatus(String statusStr) {
		this.statusStr = statusStr;
	}
	
	public String getStr() {
		return statusStr;
	}

	public static AppStatus parse(String source) {
		for (AppStatus s : AppStatus.values()) {
			if (s.getStr().equals(source)) {
				return s;
			}
		}
		return null;
	}
	
}
