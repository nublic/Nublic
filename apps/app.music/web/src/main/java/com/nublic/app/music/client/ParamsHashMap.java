package com.nublic.app.music.client;

import java.util.HashMap;


public class ParamsHashMap extends HashMap<String, String> {
	private static final long serialVersionUID = -8597576837613560863L;
	
	/**
	* Standard constructor; produces an empty KeyValueMap.
	*/
	public ParamsHashMap() {
		this("");
	}

	/**
	* Create a KeyValueMap, and initialize it with the params
	* string.
	*
	* @param params
	* A string with URL-like parameters (see below)
	*/
	public ParamsHashMap(final String params) {
		initializeWithString(params);
	}

	/**
	* Initialize a KeyValueMap with a parameters URL-like
	* string.
	*
	* @param params
	* A string formatted like param1=value1&param2=value2&... It is assumed
	* that the value has been appropriately escaped.
	*/
	void initializeWithString(String params) {
		clear();
		if ((params != null) && !params.isEmpty()) {
			String[] args = params.split("&");
			for (String element : args) {
				int equalIndex = element.indexOf("=");
				if (equalIndex == -1) {
					put(element, "");
				} else {
					put(element.substring(0, equalIndex), element.substring(equalIndex + 1));
				}
			}
		}
	}

}
