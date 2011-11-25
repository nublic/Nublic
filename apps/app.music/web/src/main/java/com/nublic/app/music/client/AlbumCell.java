package com.nublic.app.music.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

class AlbumCell extends AbstractCell<Album> {

	/**
	 * The html of the image used for contacts.
	 */
	private final String imageHtml;
	private final ImageResource albumImage;

	/**
	 * The images used for this example.
	 */

	static interface Images extends ClientBundle {
		@Source("defaultContact.jpg")
		ImageResource contact();
	}

	/**
	 * The Cell used to render a {@link ContactInfo}.
	 */

	public AlbumCell(ImageResource image) {
		this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		this.albumImage = image;
	}

	@Override
	public void render(Context context, Album value, SafeHtmlBuilder sb) {
		// Value can be null, so do a null check..
		if (value == null) {
			return;
		}

		value.setImageHtml(imageHtml);
		value.setAlbumImage(albumImage);
		AlbumWidget widget = new AlbumWidget(value);
		sb.appendHtmlConstant(widget.getElement().getInnerHTML());
	}
}
