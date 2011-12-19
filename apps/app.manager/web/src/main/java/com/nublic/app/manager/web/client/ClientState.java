package com.nublic.app.manager.web.client;

public enum ClientState {
	INITIAL,
	WELCOME,
	SETTINGS,
	FRAME;
	
	public static ClientState fromToken(String token) {
		if (token.isEmpty() || token.equals("welcome")) {
			return WELCOME;
		} else if (token.equals("settings")) {
			return SETTINGS;
		}
		return FRAME;
	}
}
