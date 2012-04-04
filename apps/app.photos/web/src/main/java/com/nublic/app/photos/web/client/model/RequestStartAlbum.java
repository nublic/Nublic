package com.nublic.app.photos.web.client.model;

class RequestStartAlbum extends Request {

	long newAlbumId;
	AlbumOrder newOrder;
	boolean force;
	
	public RequestStartAlbum(PhotosModel model, long newAlbumId, AlbumOrder newOrder, boolean force) {
		super(model);
		this.newAlbumId = newAlbumId;
		this.newOrder = newOrder;
		this.force = force;
	}
	
	@Override
	public void execute() {
		model.setCurrentAlbum(newAlbumId, newOrder, force);
		model.removeFirstRequest();
	}

}
