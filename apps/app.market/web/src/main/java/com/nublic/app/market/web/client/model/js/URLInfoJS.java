package com.nublic.app.market.web.client.model.js;

import com.google.gwt.core.client.JavaScriptObject;

//<url> ::= { "text": <translated_text>
//, "url": "http://..."
//}
public class URLInfoJS extends JavaScriptObject {
	
	protected URLInfoJS () { }
	
	public final native TranslatedTextJS getText() /*-{
		return this.text;
	}-*/;
	
	public final native String getURL() /*-{
		return this.url;
	}-*/;
}
