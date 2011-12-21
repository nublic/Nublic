package com.nublic.theme;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree.Resources;

public class GoogleLikeTreeResources implements Resources {
	
	GoogleLikeTreeBundle bundle;
	
	public GoogleLikeTreeResources() {
		bundle = GWT.create(GoogleLikeTreeBundle.class);
	}

	@Override
	public ImageResource treeClosed() {
		return bundle.arrowRight();
	}

	@Override
	public ImageResource treeLeaf() {
		return null;
	}

	@Override
	public ImageResource treeOpen() {
		return bundle.arrowDown();
	}

}
