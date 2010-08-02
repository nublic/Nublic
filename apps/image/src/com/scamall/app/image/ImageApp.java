package com.scamall.app.image;
import com.scamall.base.App;
import com.scamall.base.AppInfo;


/**
 * 
 * @author Elena Vielva
 *
 */
public class ImageApp extends App {

	/**
	 * @see com.scamall.base.App#getId()
	 */
	@Override
	public String getId() {
		return "image";
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