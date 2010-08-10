/**
 * 
 */
package com.scamall.manager;

import javax.servlet.http.HttpServletRequest;

import com.scamall.base.App;
import com.scamall.base.AppInfo;
import com.scamall.base.AppSession;
import com.scamall.base.VaadinAppSession;
import com.scamall.loader.Loader;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

/**
 * This component shows a list of loaded apps and allows the user to change to
 * them by means of a click.
 * 
 * @author Alejandro Serrano
 */
class AppList extends CustomComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4377058150397321596L;

	Loader loader;
	ManagerApplication manager;

	public AppList(Loader loader) {
		this.loader = loader;

		VerticalLayout layout = new VerticalLayout();
		for (String id : loader.getAppIds()) {
			try {
				App app = loader.getAppById(id);
				AppInfo info = app.getInfo();
				Link link = new Link(info.getName(), new ExternalResource("#" + id));
				layout.addComponent(link);
			} catch (Exception e) {
				// Do nothing
				System.err.println(e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		
		setCompositionRoot(layout);
	}
}
