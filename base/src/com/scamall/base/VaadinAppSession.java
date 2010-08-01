/**
 * 
 */
package com.scamall.base;

import com.vaadin.ui.Component;

/**
 * The parent class of all Scamall app sessions that have a web interface
 * programmed in Vaadin. You must follow the same conventions that when
 * subclassins the plain AppSession.
 * 
 * @param <A>
 *            The app class.
 * 
 * @see com.scamall.base.AppSession
 * 
 * @author Alejandro Serrano
 */
public abstract class VaadinAppSession<A extends App> extends AppSession<A> {

	/**
	 * Gets the Vaadin Component for the main app interface, that is, the one
	 * that is shown to the user when he or she clicks into the app's name in
	 * the app list.
	 * 
	 * @return The app main widget.
	 */
	public abstract Component getMainComponent();

	/**
	 * Gets the Vaadin Component for the settings interface, that is, the
	 * central place where the user searches for the different options for your
	 * app. You should try to put all those settings inside of this component
	 * instead of the main component, in order to get a better user interface.
	 * 
	 * @return The app settings widget, or null if your app does not expose any
	 *         setting to the user.
	 */
	public abstract Component getSettingsComponent();

	/**
	 * Gets the Vaadin Component for the Global Bar, that is, the little space
	 * in the top bar where the multimedia player is shown. You should try not
	 * to over-use this UI space.
	 * 
	 * @return The app global bar widget, or null if your app does not expose
	 *         any global bar component.
	 */
	public abstract Component getGlobalBarComponent();
}
