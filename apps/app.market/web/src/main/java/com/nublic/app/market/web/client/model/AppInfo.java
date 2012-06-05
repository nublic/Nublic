package com.nublic.app.market.web.client.model;

import java.util.List;

import com.nublic.app.market.web.client.Constants;
import com.nublic.app.market.web.client.model.js.AppInfoJS;

public class AppInfo {
	String id;
	String iconURL;
	TranslatedText name;
	TranslatedText shortDescription;
	TranslatedText longDescription;
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
		this.shortDescription = short_description;
		this.longDescription = long_description;
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

	public String getAppPageTarget() {
		return Constants.PARAM_APP + "=" + id;
	}

	// Getters of attributes
	public String getId() {
		return id;
	}

	public String getIconURL() {
		return iconURL;
	}

	public String getName() {
		return name.getText();
	}

	public String getShortDescription() {
		return shortDescription.getText();
	}

	public String getLongDescription() {
		return longDescription.getText();
	}

	public List<String> getScreenshotList() {
		return screenshotList;
	}

	public List<URLInfo> getLinkList() {
		return linkList;
	}

	public URLInfo getDeveloper() {
		return developer;
	}

	public String getDeb() {
		return deb;
	}

	public AppStatus getStatus() {
		return status;
	}
	
	// Setters
	public void setStatus(AppStatus status) {
		this.status = status;
	}
	
}
