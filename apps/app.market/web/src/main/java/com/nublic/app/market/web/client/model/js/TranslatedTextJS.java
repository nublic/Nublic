package com.nublic.app.market.web.client.model.js;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;


//<translated_text> ::= { "default":   "Default text for languages not stated otherwise"
//    , "translated": { "es"     : "Text for Spanish language"
//                    , ... other languages
//                    }
//    }
public class TranslatedTextJS extends JavaScriptObject {

	protected TranslatedTextJS () { }

	public final native String getDefault() /*-{
		return this['default'];
	}-*/;

	private final native JsArrayString getTranslatedLanguages() /*-{
		var r = new Array();
		if (this.translated === undefined) {
			return r;
		}
		for (var lang in this.translated) {
			if (this.translated.hasOwnProperty(lang)) {
				r.push(lang);
			}
		}
		return r;
	}-*/;
	
	private final native String getTranslatedText(String lang) /*-{
		return this.translated[lang];
	}-*/;

	public final Map<String, String> getTranslated() {
		Map<String, String> m = new HashMap<String, String>();
		JsArrayString languages = getTranslatedLanguages();
		for (int i = 0; i < languages.length(); i++) {
			m.put(languages.get(i), getTranslatedText(languages.get(i)));
		}
		return m;
	};
}
