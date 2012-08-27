package com.nublic.app.photos.mobile.client.model;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.nublic.util.gwt.LocationUtil;

public class PhotoInfo {

	private long id;
	private String title;
	private Date date;
	
	public PhotoInfo(long id, String title, Date date) {
		this.id = id;
		this.title = title;
		this.date = date;
	}
	
	public static PhotoInfo fromJson(JsonPhoto photo) {
		return new PhotoInfo(photo.getId(), photo.getTitle(), photo.getDate());
	}
	
	public long getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void changeTitle(String title) {
		this.title = title;
	}
	
	public Date getDate() {
		return date;
	}
	
	@Override
	public String toString() {
		return String.valueOf(id);
	}
	
	public static String getUrl(long id) {
		return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/thumbnail/" + id + ".png");
	}
	
	public String getUrl() {
		return PhotoInfo.getUrl(id);
	}
}
