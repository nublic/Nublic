package com.nublic.app.market.web.client.model;

import java.util.List;

import com.nublic.app.market.web.client.model.js.AppInfoJS;

public class AppInfo {
	String id;
	String iconURL;
	TranslatedText name;
	TranslatedText short_description;
	TranslatedText long_description;
	List<String> screenshotList;
	// categories;
	List<URLInfo> linkList;
	URLInfo developer;
	String deb;
	AppStatus status;

	public AppInfo(String id, String iconURL, TranslatedText name, TranslatedText short_description,
			TranslatedText long_description, List<String> screenshotList, List<URLInfo> linkList,
			URLInfo developer, String deb, AppStatus status) {
		this.id = id;
		this.iconURL = iconURL;
		this.name = name;
		this.short_description = short_description;
		this.long_description = long_description;
		this.screenshotList = screenshotList;
		this.linkList = linkList;
		this.developer = developer;
		this.deb = deb;
		this.status = status;
	}

	public AppInfo(AppInfoJS appJS) {
		this(appJS.getId(),
				appJS.getIconURL(),
				new TranslatedText(appJS.getName()),
				new TranslatedText(appJS.getShortDescription()),
				new TranslatedText(appJS.getLongDescription()),
				appJS.getScreenshots(),
				URLInfo.fromJStoList(appJS.getLinks()),
				new URLInfo(appJS.getDeveloper()),
				appJS.getDeb(),
				AppStatus.parse(appJS.getStatus()));
	}
	
}
