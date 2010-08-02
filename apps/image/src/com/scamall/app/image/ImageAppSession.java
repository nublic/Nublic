/**
 * 
 */
package com.scamall.app.image;

import com.vaadin.ui.Component;
import com.scamall.base.VaadinAppSession;

/**
 * @author yo
 *
 */
public class ImageAppSession extends VaadinAppSession<ImageApp> {

	ImageApp app;
	String user;

	/**
	 * 
	 * @param app Instance of the running {@link ImageApp} of this session
	 * @param user The user's id
	 */
	public ImageAppSession(ImageApp app, String user) {
		this.app = app;
		this.user = user;
	}

	@Override
	public Component getGlobalBarComponent() {
		return null;
	}

	@Override
	public Component getMainComponent() {
		return new ImageMainComponent();
	}

	@Override
	public Component getSettingsComponent() {
		return null;
	}

	@Override
	public ImageApp getApp() {
		return this.app;
	}

	@Override
	public String getUser() {
		return this.user;
	}

}
