package com.scamall.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scamall.base.App;
import com.scamall.base.AppSession;
import com.scamall.base.VaadinAppSession;
import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.*;

/**
 * The root of the Scamall App Manager UI.
 * 
 * @author Alejandro Serrano
 */
public class ManagerApplication extends Application implements
		HttpServletRequestListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2122121368177733417L;

	Window mainWindow;
	VerticalLayout layout;
	Label app_name;
	AppList app_list;
	Component current_component = null;

	HttpServletRequest last_request;

	@Override
	public void init() {
		// Create the UI
		mainWindow = new Window("Scamall App Manager");
		layout = new VerticalLayout();
		layout.setMargin(true);
		mainWindow.setContent(layout);

		// Create the top bar
		HorizontalLayout topBar = new HorizontalLayout();
		Link apps_link = new Link("Return to app list", new ExternalResource(
				"#apps"));
		topBar.addComponent(apps_link);
		app_name = new Label("", Label.CONTENT_XHTML);
		app_name.setWidth("100%");
		topBar.addComponent(app_name);
		topBar.setExpandRatio(app_name, 1.0f);
		layout.addComponent(topBar);

		// Create the app list
		app_list = new AppList(Singleton.getLoader());

		// Start URI fragment utility
		UriFragmentUtility uri_utility = new UriFragmentUtility();
		uri_utility.addListener(UriFragmentUtility.FragmentChangedEvent.class,
				this, "fragmentChanged");

		mainWindow.addComponent(uri_utility);
		setMainWindow(mainWindow);
	}

	public void fragmentChanged(UriFragmentUtility.FragmentChangedEvent source) {
		String fragment = source.getUriFragmentUtility().getFragment();
		if (fragment == null || fragment.equals("") || fragment.equals("apps")) {
			// Special case: show app list
			app_name.setValue("<h1>App List</h1>");
			setMainComponent(app_list);
		} else {
			// Load an app
			try {
				App app = Singleton.getLoader().getAppById(fragment);
				AppSession<? extends App> session = app.getSession(this
						.getLastRequest());
				if (session instanceof VaadinAppSession) {
					VaadinAppSession<? extends App> vas = (VaadinAppSession<? extends App>) session;
					app_name.setValue("<h1>" + app.getInfo().getName());
					setMainComponent(vas.getMainComponent());
				} else {
					app_name.setValue("<h1>This app is not visual</h1>");
					setMainComponent(new Label(""));
				}
			} catch (Exception e) {
				app_name.setValue("<h1>Error loading app</h1>");
				setMainComponent(new Label(""));
			}
		}
	}

	/**
	 * Shows a component as the main one.
	 * 
	 * @param c
	 *            The component to show.
	 */
	synchronized void setMainComponent(Component c) {
		if (current_component != null) {
			layout.replaceComponent(current_component, c);
		} else {
			layout.addComponent(c);
		}

		c.setHeight("100%");
		c.setWidth("100%");
		layout.setExpandRatio(c, 1.0f);

		current_component = c;
	}

	/* @section INTERNAL SESSION MANAGEMENT */

	HttpServletRequest getLastRequest() {
		return this.last_request;
	}

	@Override
	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		this.last_request = request;
	}

	@Override
	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		// Do nothing
	}

	/* @endsection */
}
