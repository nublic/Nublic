package com.nublic.app.photos.web.client.controller;

import com.nublic.app.photos.common.model.AlbumOrder;
import com.nublic.app.photos.web.client.Constants;
import com.nublic.util.messages.ParamsHashMap;


public class PhotoParamsHashMap extends ParamsHashMap {
	private static final long serialVersionUID = -8597576837613560863L;

	public PhotoParamsHashMap(String args) {
		super(args);
	}

	public long getAlbum() {
		if (this.containsKey("album")) {
			try {
				return Long.parseLong(this.get("album"));
			} catch (Throwable e) {
				return Constants.ALL_ALBUMS;
			}
		} else {
			return Constants.ALL_ALBUMS;
		}
	}

	public long getPhotoPosition() {
		if (this.containsKey("photo")) {
			try {
				return Long.parseLong(this.get("photo"));
			} catch (Throwable e) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public AlbumOrder getOrder() {
		if (this.containsKey("order")) {
			AlbumOrder o = AlbumOrder.fromParameter(this.get("order"));
			if (o != null) {
				return o;
			}
		}
		return null;
	}

	public View getView() {
		if (this.containsKey("view")) {
			View v = View.fromParameter(this.get("view"));
			if (v != null) {
				return v;
			}
		}
		return View.AS_ALBUMS;
	}
}