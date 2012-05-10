package com.nublic.app.photos.web.client.controller;

import java.util.HashMap;

import com.nublic.app.photos.web.client.Constants;
import com.nublic.app.photos.web.client.model.AlbumOrder;


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
	
	public long getAlbum() {
		if (this.containsKey("album")) {
			try {
				return Long.parseLong(this.get("album"));
			} catch (Throwable e) {
				return Constants.ALL_ALBUMS;
			}
		} else {
			return Constants.ALL_ALBUMS;
		}
	}
	
	public long getPhotoPosition() {
		if (this.containsKey("photo")) {
			try {
				return Long.parseLong(this.get("photo"));
			} catch (Throwable e) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public AlbumOrder getOrder() {
		if (this.containsKey("order")) {
			AlbumOrder o = AlbumOrder.fromParameter(this.get("order"));
			if (o != null) {
				return o;
			}
		}
		return null;
	}
	
	public View getView() {
		if (this.containsKey("view")) {
			View v = View.fromParameter(this.get("view"));
			if (v != null) {
				return v;
			}
		}
		return View.AS_ALBUMS;
	}
}
