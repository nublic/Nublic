package com.nublic.app.music.client.datamodel.cache;

import com.google.gwt.event.shared.EventHandler;

public interface CacheHandler<Key, Value> extends EventHandler {
	public void onCacheUpdated(Key k, Value v);
}
