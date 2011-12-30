package com.nublic.app.manager.welcome.client;

import java.util.HashMap;
import java.util.Map;

public class AppData {

	String id;
	String developer;
	String defaultName;
	Map<String, String> localizedNames;
	String path;
	boolean favourite;
	
	public AppData(String id, String developer, String defaultName, Map<String, String> localizedNames,
			String path, boolean favourite) {
		super();
		this.id = id;
		this.developer = developer;
		this.defaultName = defaultName;
		this.localizedNames = localizedNames;
		this.path = path;
		this.favourite = favourite;
	}
	
	public AppData(WebData data) {
		super();
		this.id = data.getId();
		this.developer = data.getDeveloper();
		this.defaultName = data.getDefaultName();
		this.localizedNames = new HashMap<String, String>();
		this.path = data.getPath();
		this.favourite = data.isFavourite();
	}
	
	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

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

	public String getPath() {
		return path;
	}
}
