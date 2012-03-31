package com.nublic.app.photos.web.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;


public class PhotosModel {
	
	// Singleton interface
	static PhotosModel _this = null;
	
	public static PhotosModel get() {
		if (_this == null) {
			_this = new PhotosModel();
		}
		return _this;
	}
	
	// Normal class
	public PhotosModel() {
		this.requests = new LinkedList<Request>();
		this.currentAlbum = new AlbumInfo(-1, AlbumOrder.TITLE_DESC);
	}
	
	// Queue-like interface
	// ====================
	// To use this interface, each model request must assure
	// that it calls "removeFirstRequest" at the end of its work
	
	private Queue<Request> requests;
	
	private synchronized void offerRequest(Request r) {
		requests.offer(r);
		if (requests.size() == 1) {
			// We were not doing anything
			requests.element().execute();
		}
	}
	
	synchronized void removeFirstRequest() {
		requests.remove();
		if (!requests.isEmpty()) {
			requests.element().execute();
		}
	}
	
	// Methods to be called by requests
	AlbumInfo currentAlbum;
	
	AlbumInfo getCurrentAlbum() {
		return currentAlbum;
	}
	
	void setCurrentAlbum(long albumId, AlbumOrder order) {
		this.currentAlbum = new AlbumInfo(albumId, order);
	}
	
	void updatePhotos(JsonRowCount info, long initialPos) {
		currentAlbum.setRowCount(info.getRowCount());
		for (int i = 0; i < info.getPhotos().length(); i++) {
			currentAlbum.addPhoto(i + initialPos, PhotoInfo.fromJson(info.getPhotos().get(i)));
		}
	}
	
	// Outside-word models
	public void startNewAlbum(long album, AlbumOrder order) {
		offerRequest(new RequestStartAlbum(this, album, order));
	}
	
	public void rowCount(CallbackRowCount cb) {
		offerRequest(new RequestRowCount(this, cb));
	}
	
	public void photoList(long start, long length, CallbackListOfPhotos cb) {
		offerRequest(new RequestListOfPhotos(this, cb, start, length));
	}
	
	public void photo(long position, CallbackOnePhoto cb) {
		offerRequest(new RequestOnePhoto(this, cb, position));
	}

	// Album cache management
	private Object albumLock = new Object();
	private boolean isAlbumListDownloaded = false;
	private boolean isAlbumListDownloading = false;
	private List<CallbackListOfAlbums> tmpAlbumsCb = new ArrayList<CallbackListOfAlbums>();
	private Multimap<Long, CallbackOneAlbum> tmpOneAlbumCb = ArrayListMultimap.create();
	private Map<Long, String> albumCache = new HashMap<Long, String>();
	
	private void startDownloadingAlbumCache() {
		isAlbumListDownloading = true;
		
		SequenceHelper.sendJustOne(new Message() {
			@Override
			public String getURL() {
				return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/albums");
			}

			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					synchronized(albumLock) {
						JsArray<JsonAlbum> albums = JsonUtils.safeEval(response.getText());
						for (int i = 0; i < albums.length(); i++) {
							JsonAlbum json_album = albums.get(i);
							albumCache.put((long)json_album.getId(), json_album.getName());
						}
						isAlbumListDownloaded = true;
						isAlbumListDownloading = false;
						// Send callbacks
						for (CallbackListOfAlbums cb : tmpAlbumsCb) {
							cb.list(albumCache);
						}
						tmpAlbumsCb.clear();
						for (Entry<Long, CallbackOneAlbum> cb : tmpOneAlbumCb.entries()) {
							if (albumCache.containsKey(cb.getKey())) {
								cb.getValue().list(cb.getKey(), albumCache.get(cb.getKey()));
							} else {
								cb.getValue().error();
							}
						}
						tmpOneAlbumCb.clear();
					}
				} else {
					onError();
				}
			}

			@Override
			public void onError() {
				synchronized(albumLock) {
					for (CallbackListOfAlbums cb : tmpAlbumsCb) {
						cb.error();
					}
					tmpAlbumsCb.clear();
					for (Entry<Long, CallbackOneAlbum> cb : tmpOneAlbumCb.entries()) {
						cb.getValue().error();
					}
					tmpOneAlbumCb.clear();
					// Tell we haven't downloaded
					isAlbumListDownloaded = false;
					isAlbumListDownloading = false;
				}
			}
		}, RequestBuilder.GET);
	}
	
	public void album(long id, CallbackOneAlbum cb) {
		synchronized(albumLock) {
			if (isAlbumListDownloaded) {
				if (albumCache.containsKey(id)) {
					cb.list(id, albumCache.get(id));
				} else {
					cb.error();
				}
			} else {
				tmpOneAlbumCb.put(id, cb);
				if (!isAlbumListDownloading) {
					startDownloadingAlbumCache();
				}
			}
		}
	}
	
	public void albums(CallbackListOfAlbums cb) {
		synchronized(albumLock) {
			if (isAlbumListDownloaded) {
				cb.list(albumCache);
			} else {
				tmpAlbumsCb.add(cb);
				if (!isAlbumListDownloading) {
					startDownloadingAlbumCache();
				}
			}
		}
	}
	
	public void newAlbum(final String name, final CallbackOneAlbum cb) {
		// Don't allow to send already-existing album ids
		if (albumCache.containsValue(name)) {
			cb.error();
			return;
		}
		
		SequenceHelper.sendJustOne(new Message() {
			
			@Override
			public String getURL() {
				addParam("name", name);
				return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/albums");
			}
			
			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					long newId = Long.valueOf(response.getText());
					// Again, don't allow duplicate album names
					if (!albumCache.containsKey(newId)) {
						albumCache.put(newId, name);
						cb.list(newId, name);
					} else {
						cb.error();
					}
				} else {
					cb.error();
				}
			}
			
			@Override
			public void onError() {
				cb.error();
			}
			
		}, RequestBuilder.PUT);
	}
}
