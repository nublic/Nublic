package com.nublic.app.manager.web.frame;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.NamedFrame;
import com.nublic.app.manager.web.client.LocationWithHash;

public class AppFrame extends NamedFrame {
	
	private static Counter counter = new Counter();
	
	// To distinguish between frames
	long id;
	// To handle Url changes
	ArrayList<AppUrlChangeHandler> urlChangeHandlers;
	String lastUrl;
	String lastTitle;

	public AppFrame(String name) {
		super(name);
		
		id = counter.next();
		
		urlChangeHandlers = new ArrayList<AppUrlChangeHandler>();
		lastUrl = "about:blank";
		lastTitle = "";
		
		// Set timer to check for URL changes
		Timer urlChangeTimer = new Timer() {
			@Override
			public synchronized void run() {			
				ExtendedFrameElement e = AppFrame.this.getElement().cast();
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
	
	public String getHref() {
		ExtendedFrameElement e = this.getElement().cast();
		return e.getLocationHref();
	}
	
	public void setHref(String url) {		
		ExtendedFrameElement e = this.getElement().cast();
		LocationWithHash now = new LocationWithHash(e.getLocationHref());
		LocationWithHash then = new LocationWithHash(url);
		if (now.sameBase(then)) {
			if (!now.sameHash(then)) {
				e.setLocationHash(then.getHash());
			}
			// If the URL is completely equal, we should not change it
		} else {
			this.setUrl(url);
		}
	}
	
	public void back() {
		ExtendedFrameElement e = this.getElement().cast();
		e.back();
	}
	
	public void forward() {
		ExtendedFrameElement e = this.getElement().cast();
		e.forward();
	}
	
	public long getId() {
		return this.id;
	}
}
