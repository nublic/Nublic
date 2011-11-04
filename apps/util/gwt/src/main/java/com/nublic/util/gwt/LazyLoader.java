package com.nublic.util.gwt;

import java.util.HashSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.RootPanel;

public class LazyLoader {
	
	private ArrayListMultimap<String, Callback<Event>> callbacks;
	private HashSet<String> alreadyLoaded;
	
	public LazyLoader() {
		callbacks = ArrayListMultimap.create();
		alreadyLoaded = new HashSet<String>();
	}
	
	public void loadJS(final String url, final Callback<Event> callback) {
		if (alreadyLoaded.contains(url)) {
			callback.execute(null);
		} else {
			if (!callbacks.containsKey(url)) {
				callbacks.put(url, callback);
				Element e = DOM.createElement("script"); 
			    DOM.setElementProperty(e, "type", "text/javascript"); 
			    DOM.setElementProperty(e, "src", url);
			    DOM.sinkEvents(e, Event.ONLOAD);
			    DOM.setEventListener(e, new EventListener() {
					@Override
					public void onBrowserEvent(Event e) {
						if (e.getTypeInt() == Event.ONLOAD) {
							alreadyLoaded.add(url);
							for (Callback<Event> cb : callbacks.get(url)) {
								cb.execute(e);
							}
							callbacks.removeAll(url);
						}
					}
				});
			    // scriptTags.put(uniqueId, e); 
			    DOM.appendChild(RootPanel.get().getElement(), e);
			} else {
				callbacks.put(url, callback);
			}
		}
	}
}
