package com.nublic.app.photos.web.client.model;

import java.util.LinkedList;
import java.util.Queue;


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
}
