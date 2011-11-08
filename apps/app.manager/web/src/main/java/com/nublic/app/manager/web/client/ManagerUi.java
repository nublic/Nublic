package com.nublic.app.manager.web.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.web.frame.AppFrame;
import com.nublic.app.manager.web.frame.AppUrlChangeEvent;
import com.nublic.app.manager.web.frame.AppUrlChangeHandler;
import com.nublic.app.manager.web.frame.Counter;
import com.nublic.app.manager.web.welcome.WelcomePage;

public class ManagerUi extends Composite implements AppUrlChangeHandler {
	
	interface Styles extends CssResource {
	    String inner();
	}
	@UiField Styles style;

	private static ManagerUiUiBinder uiBinder = GWT.create(ManagerUiUiBinder.class);
	@UiField TabBar appBar;
	@UiField LayoutPanel layout;
	@UiField Image image;
	Widget innerWidget = null;
	// Model
	ExtendedHistory history;
	HashMap<String, AppData> apps;
	ClientState state;
	String path;

	interface ManagerUiUiBinder extends UiBinder<Widget, ManagerUi> {
	}

	public ManagerUi() {
		initWidget(uiBinder.createAndBindUi(this));	
		// Initialize the model
		// Get apps from server
		this.history = new ExtendedHistory(Location.getHref());
		this.state = ClientState.INITIAL;
		this.path = "";
	}
	
	void setContentWidget(Widget w) {
		if (innerWidget != null) {
			layout.remove(innerWidget);
		}
		innerWidget = w;
		innerWidget.setHeight("100%");
		innerWidget.setWidth("100%");
		layout.add(innerWidget);
		layout.setWidgetTopBottom(innerWidget, 50, Unit.PX, 0, Unit.PX);
	}
	
	public void go(String token) {
		// Generate final URL
		String current = Location.getHref();
		ClientState newState = ClientState.fromToken(token);
		String newHref = GWT.getHostPageBaseURL() + token;
		long innerId = innerWidget instanceof AppFrame ? ((AppFrame)innerWidget).getId() : Counter.NOT_ALLOWED;
		if (history.isCurrent(current) && !history.isBareNew()) {
			/* Do nothing */
		} else if (history.isPrevious(current)) {
			if (newState == ClientState.FRAME && state == ClientState.FRAME) {
				AppFrame frame = (AppFrame)innerWidget;
				if (history.isPreviousId(innerId)) {
					frame.back();
				} else {
					changeTo(newState, newHref);
				}
			} else {
				changeTo(newState, newHref);
			}
			history.back();
		} else if (history.isNext(current)) {
			if (newState == ClientState.FRAME && state == ClientState.FRAME) {
				AppFrame frame = (AppFrame)innerWidget;
				if (history.isPreviousId(innerId)) {
					frame.forward();
				} else {
					changeTo(newState, newHref);
				}
			} else {
				changeTo(newState, newHref);
			}
			history.forward();
		} else {
			changeTo(newState, newHref);
			innerId = innerWidget instanceof AppFrame ? ((AppFrame)innerWidget).getId() : Counter.NOT_ALLOWED;
			history.go(current, innerId);
		}
	}
	
	void changeTo(ClientState newState, String path) {
		if (newState != state) {
			setContentWidget(createWidget(newState));
			if (newState == ClientState.FRAME) {
				AppFrame frame = (AppFrame)innerWidget;
				frame.setHref(path);
			}
			this.state = newState;
			this.path = path;
		}
	}
	
	Widget createWidget(ClientState newState) {
		switch(newState) {
		case INITIAL:
			return null;
		case WELCOME:
			return new WelcomePage();
		case FRAME:
			AppFrame frame = new AppFrame("inner");
			frame.addAppUrlChangedHandler(this);
			frame.setStyleName(style.inner());
			return frame;
		}
		return null;
	}

	@Override
	public void appUrlChanged(AppUrlChangeEvent event) {
		String path = event.getUrl().replace(GWT.getHostPageBaseURL(), "");
		// Generate final URL
		LocationWithHash location = new LocationWithHash(Location.getHref());
		LocationWithHash newLocation = new LocationWithHash(location.getBase(), path);
		String finalNewPath = newLocation.getLocation();
		// Check in extended history
		if (history.isCurrent(finalNewPath)) {
			// Do nothing
		} else if (history.isPrevious(finalNewPath)) {
			History.back();
		} else if (history.isNext(finalNewPath)) {
			History.forward();
		} else {
			History.newItem(path);
		}
	}

	@Override
	public void appTitleChanged(AppUrlChangeEvent event) {
		String trimmed = event.getTitle().trim();
		String title = trimmed.isEmpty() ? "Nublic" : "Nublic - " + trimmed;
		Document.get().setTitle(title);
	}
}
