package com.nublic.app.manager.web.client;

import java.util.Map;

public class AppData {

	String id;
	String name;
	String developer;
	String defaultName;
	Map<String, String> localizedNames;
	String path;
	boolean favourite;
	
	public AppData(String id, String name, String developer, String defaultName, Map<String, String> localizedNames,
			String path, boolean favourite) {
		super();
		this.id = id;
		this.name = name;
		this.developer = developer;
		this.defaultName = defaultName;
		this.localizedNames = localizedNames;
		this.path = path;
		this.favourite = favourite;
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

	public String getName() {
		return name;
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
