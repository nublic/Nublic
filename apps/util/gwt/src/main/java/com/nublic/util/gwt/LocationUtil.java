package com.nublic.util.gwt;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;

public class LocationUtil {
	public static String getHostBaseUrl() { 
		return Location.getProtocol() + "//" + Location.getHost() + "/";
	}
	
	public static String replaceQuestionMarks(String s) {
		return s.replace("?", "%3F");
	}
	
	public static String encodeURL(String url) {
		return replaceQuestionMarks(URL.encode(url));
	}
}
