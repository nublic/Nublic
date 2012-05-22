package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/up.png")
	ImageResource up();
	
	@Source("images/down.png")
	ImageResource down();
	
	@Source("images/download.png")
	ImageResource download();
	
	@Source("images/view.png")
	ImageResource view();
	
	@Source("images/multiple-selection.png")
	DataResource multipleSelection();
	
	@Source("images/paste.png")
	ImageResource paste();
	
	@Source("images/addfile.png")
	ImageResource addfile();
	
	@Source("images/newfolder.png")
	ImageResource newfolder();
	
	@Source("images/mirror.png")
	ImageResource mirror();
	
	@Source("images/synced.png")
	ImageResource synced();
	
	@Source("images/nublic-only.png")
	ImageResource nublicOnly();
	
//	@Source("images/home.png")
//	ImageResource home();
	
	@Source("images/home.png")
	DataResource home();
	
	@Source("images/document_view.png")
	ImageResource documentView();
	
	@Source("images/edit_clear.png")
	ImageResource editClear();
	
	@Source("images/edit_copy.png")
	ImageResource editCopy();
	
	@Source("images/edit_cut.png")
	ImageResource editCut();
	
	@Source("images/edit_delete.png")
	ImageResource editDelete();
	
	@Source("images/edit_paste.png")
	ImageResource editPaste();
	
	@Source("images/edit_rename.png")
	ImageResource editRename();
	
	@Source("images/filter.png")
	ImageResource filter();
	
	@Source("images/folder_download.png")
	ImageResource folderDownload();
	
	@Source("images/image_view.png")
	ImageResource imageView();
	
	@Source("images/music_view.png")
	ImageResource musicView();
	
	@Source("images/select_all.png")
	ImageResource selectAll();
	
	@Source("images/text_view.png")
	ImageResource textView();
	
	@Source("images/unselect_all.png")
	ImageResource unselectAll();
	
	@Source("images/video_view.png")
	ImageResource videoView();
	
	@Source("images/copy_addon.png")
	ImageResource copyAddon();
}