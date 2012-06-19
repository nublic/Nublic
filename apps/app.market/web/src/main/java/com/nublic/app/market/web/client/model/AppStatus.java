package com.nublic.app.market.web.client.model;

import com.nublic.app.market.web.client.Constants;

//<package_status> ::= "does-not-exist"
//| "installed"
//| "installing"
//| "removing"
//| "not-installed"
//| "error" (was tried to install and did not success)
public enum AppStatus {
	DOESNT_EXIST("does-not-exist",
			Constants.I18N.doesntExist(),
			Constants.ERROR_STYLE),
	INSTALLED("installed",
			Constants.I18N.installed(),
			Constants.INSTALLED_STYLE,
			Constants.I18N.installedHover(),
			Constants.INSTALLED_HOVER_STYLE),
	INSTALLING("installing",
			Constants.I18N.installing(),
			Constants.INSTALLING_STYLE),
	REMOVING("removing",
			Constants.I18N.removing(),
			Constants.INSTALLING_STYLE),
	NOT_INSTALLED("not-installed",
			Constants.I18N.notInstalled(),
			Constants.NOT_INSTALLED_STYLE,
			Constants.I18N.notInstalledHover(),
			Constants.NOT_INSTALLED_HOVER_STYLE),
	ERROR("error",
			Constants.I18N.error(),
			Constants.ERROR_STYLE);
	
	String statusStr;

	String i18nStr;
	String css;
	String hoverStr;
	String hoverCss;
	
	AppStatus(String statusStr, String i18nStr, String css) {
		this(statusStr, i18nStr, css, "", "");
	}
	
	AppStatus(String statusStr, String i18nStr, String css, String hoverStr, String hoverCss) {
		this.statusStr = statusStr;

		this.i18nStr = i18nStr;
		this.css = css;
		this.hoverStr = hoverStr;
		this.hoverCss = hoverCss;
	}
	
	public String getStr() {
		return statusStr;
	}

	public String getI18NStr() {
		return i18nStr;
	}

	public boolean isClickable() {
		return !hoverStr.isEmpty();
	}

	public String getCss() {
		return css;
	}

	public String getHoverStr() {
		return hoverStr;
	}

	public String getHoverCss() {
		return hoverCss;
	}

	public static AppStatus parse(String source) {
		for (AppStatus s : AppStatus.values()) {
			if (s.getStr().equals(source)) {
				return s;
			}
		}
		return ERROR;
	}
	
}
