package com.nublic.app.market.web.client.model.js;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

//<package> ::= { "id": "unique id for the app"
//, "icon": "absolute URL to icon"
//, "name": <translated_text>
//, "short_description": <translated_text>
//, "long_description": <translated_text>
//, "screenshots": [ "URL to first shot", "URL to second shot", ... ]
//, "categories": [ "category_1", "category_2", ... ] (currently not used)
//, "links": [ <url>, <url>, ... ]
//, "developer": <url>
//, "deb": "deb package to get the app installed"
//, "status": <package_status> (only for Market server, not for apps descriptor)
//}
public class AppInfoJS extends JavaScriptObject {

	protected AppInfoJS () { }

	public final native String getIconURL() /*-{
		return this.icon;
	}-*/;

	public final native TranslatedTextJS getName() /*-{
		return this.name;
	}-*/;
	
	public final native TranslatedTextJS getShortDescription() /*-{
		return this.short_description;
	}-*/;
	
	public final native TranslatedTextJS getLongDescription() /*-{
		return this.long_description;
	}-*/;
	
	public final native JsArrayString _getScreenshots() /*-{
		return this.screenshots;
	}-*/;

	public final List<String> getScreenshots() {
		List<String> screenshotList = new ArrayList<String>();
		JsArrayString jsList = _getScreenshots();
		for (int i = 0; i < jsList.length(); i++) {
			screenshotList.add(jsList.get(i));
		}
		return screenshotList;
	}
	
	// Categories (not yet implemented)

	public final native JsArray<URLInfoJS> getLinks() /*-{
		return this.links;
	}-*/;
	
	public final native URLInfoJS getDeveloper() /*-{
		return this.developer;
	}-*/;
	
	public final native String getDeb() /*-{
		return this.deb;
	}-*/;
	
	public final native String getStatus() /*-{
		return this.status;
	}-*/;
}
