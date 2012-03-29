package com.nublic.app.photos.web.client.model;

class RequestStartAlbum extends Request {

	long newAlbumId;
	AlbumOrder newOrder;
	
	public RequestStartAlbum(PhotosModel model, long newAlbumId, AlbumOrder newOrder) {
		super(model);
		this.newAlbumId = newAlbumId;
		this.newOrder = newOrder;
	}
	
	@Override
	public void execute() {
		model.setCurrentAlbum(newAlbumId, newOrder);
		model.removeFirstRequest();
	}

}
