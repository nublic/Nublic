package com.nublic.util.gwt;

import com.google.gwt.user.client.Window.Location;

public class LocationUtil {
	public static String getHostBaseUrl() { 
		return Location.getProtocol() + "//" + Location.getHost() + "/";
	}
}
