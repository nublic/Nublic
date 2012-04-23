package com.nublic.app.music.client.controller;

import com.nublic.app.music.client.Constants;

public enum ViewKind {
	ARTISTS(Constants.VIEW_ARTISTS),
	ALBUMS(Constants.VIEW_ALBUMS),
	SONGS(Constants.VIEW_SONGS);
	
	String viewStr;
	
	private ViewKind(String viewStr) {
		this.viewStr = viewStr;
	}
	
	public static ViewKind parse(String s) {
		for (ViewKind vk : values()) {
			if (vk.viewStr.equals(s)) {
				return vk;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return viewStr;
	}
}
