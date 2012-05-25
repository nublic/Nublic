package com.nublic.app.market.web.client.model;

import java.util.Map;

import com.google.gwt.i18n.client.LocaleInfo;
import com.nublic.app.market.web.client.model.js.TranslatedTextJS;

//<translated_text> ::= { "default":   "Default text for languages not stated otherwise"
//, "translated": { "es"     : "Text for Spanish language"
//              , ... other languages
//              }
//}
public class TranslatedText {
	String defaultText;
	Map<String, String> translated;

	public TranslatedText(String defaultText, Map<String, String> translated) {
		this.defaultText = defaultText;
		this.translated = translated;
	}

	public TranslatedText(TranslatedTextJS jsText) {
		this(jsText.getDefault(), jsText.getTranslated());
	}

	public String getText(String langCode) {
		String translatedText = translated.get(langCode);
		if (translatedText == null) {
			// If we don't find the country try at least the language locale
			String languageText = translated.get(langCode.split("_")[0]);
			return languageText == null ? defaultText : languageText;
		} else {
			return translatedText;
		}
	}

	public String getText() {
		LocaleInfo info = LocaleInfo.getCurrentLocale();
		return getText(info.getLocaleName());
	}

}
