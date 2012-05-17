package com.nublic.app.manager.welcome.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class WebData extends JavaScriptObject {
	
	protected WebData () { }
	
	public final native String getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getDefaultName() /*-{
	 	return this.name['default'];
	}-*/;
	
	public final native String getLocalizedName(String language) /*-{
	 	return this.name.localized[language];
	}-*/;
	
	public final native JsArrayString getLocalizedLanguages() /*-{
		var r = new Array();
		for (var lang in this.name.localized) {
    		if(this.name.localized.hasOwnProperty(lang)) {
    			r.push(lang);
			}
		}
		return r;
	}-*/;
	
	public final native String getDeveloper() /*-{
		return this.developer;
	}-*/;
	
	public final native String getPath() /*-{
		return this.path;
	}-*/;
	
	public final native boolean isFavourite() /*-{
		return this.favourite;
	}-*/;
}
