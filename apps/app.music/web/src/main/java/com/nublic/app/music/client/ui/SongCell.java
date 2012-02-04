package com.nublic.app.music.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.nublic.app.music.client.datamodel.Song;

public class SongCell extends AbstractCell<Song> {
	@Override
	public void render(Context context, Song value, SafeHtmlBuilder sb) {
		if (value == null) {
			return;
		}
		
		Anchor a = new Anchor(value.getTitle());
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO: Play song;
			}
		});
		
		sb.appendHtmlConstant("<div>");
		sb.appendHtmlConstant(a.getHTML()); // TODO: They don't recommend this..
		sb.appendHtmlConstant("</div>");
	}
}