package com.nublic.app.manager.web.client;

public enum ClientState {
	INITIAL,
	WELCOME,
	FRAME;
	
	public static ClientState fromToken(String token) {
		if (token.isEmpty() || token.equals("welcome")) {
			return WELCOME;
		}
		return FRAME;
	}
}
