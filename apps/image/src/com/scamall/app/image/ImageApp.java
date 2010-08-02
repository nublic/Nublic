package com.scamall.app.image;
import com.scamall.base.App;
import com.scamall.base.AppInfo;

public class ImageApp extends App {

	/**
	 * @see com.scamall.base.App#getId()
	 */
	@Override
	public String getId() {
		return "example";
	}

	/**
	 * @see com.scamall.base.App#getInfo()
	 */
	@Override
	public AppInfo getInfo() {
		return new ImageAppInfo();
	}

	/**
	 * @see com.scamall.base.App#newSession(java.lang.String)
	 */
	@Override
	protected ImageAppSession newSession(String user) {
		return new ImageAppSession(this, user);
	}

}