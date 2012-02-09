package com.nublic.util.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public abstract class Cache<Key, Value> {
	private HashMap<Key, Value> cache = new HashMap<Key, Value>();
//	private ArrayList<CacheHandler<Key, Value>> handlerList = new ArrayList<CacheHandler<Key, Value>>();
	// <Key, CacheHandler<Key, Value>>
	private Multimap<Key, CacheHandler<Key, Value>> handlerMap = HashMultimap.<Key, CacheHandler<Key, Value>>create();
	private Object synchronizeHandlers = new Object();
	private Set<Key> keysBeingAsked = new HashSet<Key>();
	private Object synchronizeKeys = new Object();
	
	public void addHandler(Key k, CacheHandler<Key, Value> h) {
		synchronized (synchronizeHandlers) {
			handlerMap.put(k, h);
		}
	}
	
	public void put(Key k, Value v) {
		cache.put(k, v);
	}

	public void obtain(final Key k) {
		synchronized (synchronizeKeys) {
			Value v = cache.get(k);
			if (v == null) {
				if (!keysBeingAsked.contains(k)) {
					keysBeingAsked.add(k);
					Message m = new Message() {
						@Override
						public void onSuccess(Response response) {
//							if (response.getStatusCode() == Response.SC_OK) {
								synchronized (synchronizeKeys) {
									keysBeingAsked.remove(k);
									Value v = getValue(response);
									cache.put(k, v);
									callHandlers(k, v);
								}
//							} else {
//								error(k);
//							}
						}
						@Override
						public void onError() {
							// TODO: real things
//							error(k);
							onSuccess(null);
						}
						@Override
						public String getURL() {
							return Cache.this.getURL(k);
						}
					};
					SequenceHelper.sendJustOne(m, RequestBuilder.GET);
				}
			} else {
				callHandlers(k, v);
			}
		}
	}
	
//	private void error(Key k) {
//		synchronized (synchronizeKeys) {
//			keysBeingAsked.remove(k);
//			throw new IllegalArgumentException();
//		}
//	}
	
	private void callHandlers(Key k, Value v) {
		synchronized (synchronizeHandlers) {
			Collection<CacheHandler<Key, Value>> handlers = handlerMap.get(k);
			for (CacheHandler<Key, Value> h : handlers) {
				h.onCacheUpdated(k, v);
			}
			handlerMap.removeAll(k);
		}
	}
	
	// Indicates the url to call to get the value of the key k.
	public abstract String getURL(Key k);
	public abstract Value getValue(Response r);

}
