package com.nublic.app.manager.web.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;

public class ManagerUi extends Composite {

	private static ManagerUiUiBinder uiBinder = GWT.create(ManagerUiUiBinder.class);
	@UiField NamedFrame innerFrame;
	@UiField TabBar appBar;
	// To save app information
	ArrayList<String> appIds;
	HashMap<String, String> appUrls;
	// To handle Url changes
	ArrayList<AppUrlChangeHandler> urlChangeHandlers;
	String lastUrl;
	String lastTitle;

	interface ManagerUiUiBinder extends UiBinder<Widget, ManagerUi> {
	}

	public ManagerUi() {
		initWidget(uiBinder.createAndBindUi(this));
		
		appIds = new ArrayList<String>();
		appUrls = new HashMap<String, String>();
		
		urlChangeHandlers = new ArrayList<AppUrlChangeHandler>();
		lastUrl = "about:blank";
		lastTitle = "";
		
		// Set timer to check for URL changes
		Timer urlChangeTimer = new Timer() {
			@Override
			public synchronized void run() {
				ExtendedFrameElement e = innerFrame.getElement().cast();
				AppUrlChangeEvent event = new AppUrlChangeEvent(e.getLocationHref(),
						e.getDocumentTitle(), e.getLocationHash());
				if (!lastUrl.equals(e.getLocationHref())) {
					lastUrl = e.getLocationHref();
					for (AppUrlChangeHandler handler : urlChangeHandlers) {
						handler.appUrlChanged(event);
					}
				}
				if (!lastTitle.equals(e.getDocumentTitle())) {
					lastTitle = e.getDocumentTitle();
					for (AppUrlChangeHandler handler : urlChangeHandlers) {
						handler.appTitleChanged(event);
					}
				}
			}
		};
		urlChangeTimer.scheduleRepeating(500);
	}
	
	public void addAppUrlChangedHandler(AppUrlChangeHandler handler) {
		this.urlChangeHandlers.add(handler);
	}
	
	public void removeAppUrlChangedHandler(AppUrlChangeHandler handler) {
		this.urlChangeHandlers.remove(handler);
	}
	
	public void setFrameUrl(String url) {
		ExtendedFrameElement e = innerFrame.getElement().cast();
		LocationWithHash now = new LocationWithHash(e.getLocationHref());
		LocationWithHash then = new LocationWithHash(url);
		if (now.sameBase(then)) {
			e.setLocationHash(then.getHash());
		} else {
			innerFrame.setUrl(url);
		}
	}
	
	public void select(String id) {
		int tabNumber = appIds.indexOf(id);
		appBar.selectTab(tabNumber);
	}
	
	public void select(String id, String url) {
		int tabNumber = appIds.indexOf(id);
		appBar.selectTab(tabNumber, false);
		setFrameUrl(url);
	}

	public void addTab(String id, String name, String image, String url) {
		HorizontalPanel tab = new HorizontalPanel();
		tab.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		tab.add(new Image(image));
		tab.add(new Label(name));
		appBar.addTab(tab);
		
		appIds.add(id);
		appUrls.put(id, url);
	}
	
	@UiHandler("appBar")
	void onAppBarSelection(SelectionEvent<Integer> event) {
		int number = appBar.getSelectedTab();
		String id = appIds.get(number);
		String url = appUrls.get(id);
		this.setFrameUrl(url);
	}
}
