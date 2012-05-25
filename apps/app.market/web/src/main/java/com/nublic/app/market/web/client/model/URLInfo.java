package com.nublic.app.market.web.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.nublic.app.market.web.client.model.js.URLInfoJS;


//<url> ::= { "text": <translated_text>
//, "url": "http://..."
//}
public class URLInfo {
	TranslatedText text;
	String url;

	public URLInfo(TranslatedText text, String url) {
		this.text = text;
		this.url = url;
	}

	public URLInfo(URLInfoJS jsInfo) {
		this(new TranslatedText(jsInfo.getText()), jsInfo.getURL());
	}

	public static List<URLInfo> fromJStoList(JsArray<URLInfoJS> links) {
		List<URLInfo> urlList = new ArrayList<URLInfo>();
		for (int i = 0; i < links.length(); i++) {
			urlList.add(new URLInfo(links.get(i)));
		}
		return urlList;
	}

}
