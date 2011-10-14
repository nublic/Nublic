package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FolderContent extends JavaScriptObject {

	protected FolderContent () { }
	
//	public static final native void sort(FolderContent content) /*-{
//		this.sortList(content.subfolders);
//	}-*/;
//	
//	public static final native void sortList(JsArray<FolderContent> contentList) /*-{
//		for (var i = 0; i < contentList.length; i++) {
//			var subnode = contentList[i];
//			this.sort(subnode);
//		}
//		var f = function(a, b) {
//			var x = a.name.toLowerCase();
//			var y = b.name.toLowerCase();
//			return ((x < y) ? -1 : ( (x > y) ? 1 : 0 )); 
//		};
//		contentList.sort(f);
//	}-*/;
//	
	public final native String getName() /*-{
		return this.name;
    }-*/;

	public final native JsArray<FolderContent> getSubfolders() /*-{
	 	return this.subfolders;
    }-*/;

}
