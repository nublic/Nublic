package com.nublic.util.gwt;

import java.util.HashSet;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.RootPanel;

public class LazyLoader {
	
	private HashSet<String> alreadyLoaded;
	
	public LazyLoader() {
		alreadyLoaded = new HashSet<String>();
	}
	
	public void loadJS(String url, final Callback<Event> callback) {
		if (!alreadyLoaded.contains(url)) {
			alreadyLoaded.add(url);
			Element e = DOM.createElement("script"); 
		    DOM.setElementProperty(e, "type", "text/javascript"); 
		    DOM.setElementProperty(e, "src", url);
		    DOM.sinkEvents(e, Event.ONLOAD);
		    DOM.setEventListener(e, new EventListener() {
				@Override
				public void onBrowserEvent(Event e) {
					if (e.getTypeInt() == Event.ONLOAD) {
						callback.execute(e);
					}
				}
			});
		    // scriptTags.put(uniqueId, e); 
		    DOM.appendChild(RootPanel.get().getElement(), e);
		}
	}
}
