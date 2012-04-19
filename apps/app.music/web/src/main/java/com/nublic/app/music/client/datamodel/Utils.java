package com.nublic.app.music.client.datamodel;

import java.util.HashMap;

import com.nublic.app.music.client.Constants;

public class Utils {
	public static String getTargetHistoryToken(String artistId, String albumId, String collectionId, String viewKind) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (artistId != null) {
			params.put(Constants.PARAM_ARTIST, artistId);
		}
		if (albumId != null) {
			params.put(Constants.PARAM_ALBUM, albumId);
		}
		if (collectionId != null) {
			params.put(Constants.PARAM_COLLECTION, collectionId);
		}
		if (viewKind != null) {
			params.put(Constants.PARAM_VIEW, viewKind);
		}
		return getTargetHistoryToken(params);
	}
	
	public static String getTargetHistoryToken(HashMap<String, String> params) {
		StringBuilder target = new StringBuilder();
		for (String key : params.keySet()) {
			if (target.length() != 0) {
				target.append("&");
			}
			target.append(key);
			target.append("=");
			target.append(params.get(key));
		}
		return target.toString();
	}
}
