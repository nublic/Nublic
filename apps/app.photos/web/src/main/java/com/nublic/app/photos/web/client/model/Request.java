package com.nublic.app.photos.web.client.model;

abstract class Request {
	
	protected PhotosModel model;
	
	protected Request(PhotosModel model) {
		this.model = model;
	}
	
	public abstract void execute();
}
