package com.nublic.util.cache;

import com.google.gwt.event.shared.EventHandler;

public interface CacheHandler<Key, Value> extends EventHandler {
	public void onCacheUpdated(Key k, Value v);
}
