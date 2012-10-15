package com.nublic.app.manager.web.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.LocaleInfo;

public class AppData {

	String id;
	String developer;
	String defaultName;
	Map<String, String> localizedNames;
	String webPath;
	String webExtra;
	
	public AppData(String id, String developer, String defaultName, Map<String, String> localizedNames,
			String webPath, String webExtra) {
		super();
		this.id = id;
		this.developer = developer;
		this.defaultName = defaultName;
		this.localizedNames = localizedNames;
		this.webPath = webPath;
		this.webExtra = webExtra;
	}
	
	public AppData(WebData data) {
		super();
		this.id = data.getId();
		this.developer = data.getDeveloper();
		this.defaultName = data.getDefaultName();
		this.localizedNames = new HashMap<String, String>();
		JsArrayString langs = data.getLocalizedLanguages();
		for (int i = 0; i < langs.length(); i++) {
			String lang = langs.get(i);
			this.localizedNames.put(lang, data.getLocalizedName(lang));
		}
		this.webPath = data.getWebPath();
		this.webExtra = data.getWebExtra();
	}
	
	/*public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}*/

	public String getId() {
		return id;
	}

	public String getDeveloper() {
		return developer;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public Map<String, String> getLocalizedNames() {
		return localizedNames;
	}
	
	public String getLocalizedName() {
		String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
		// First, try to get the entire locale
		if (localizedNames.containsKey(currentLocale)) {
			return localizedNames.get(currentLocale);
		}
		// Now try to get only the language name
		String currentLanguage = currentLocale.split("_")[0];
		if (localizedNames.containsKey(currentLanguage)) {
			return localizedNames.get(currentLanguage);
		}
		// Else get the default
		return defaultName;
	}

	public String getWebPath() {
		return webPath;
	}
	
	public String getWebExtra() {
		return webExtra;
	}
}