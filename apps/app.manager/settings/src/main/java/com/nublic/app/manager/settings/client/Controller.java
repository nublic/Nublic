package com.nublic.app.manager.settings.client;

import com.nublic.util.messages.ParamsHashMap;

public class Controller {
	public static Controller INSTANCE = null;

	public static Controller create() {
		if (INSTANCE == null) {
			INSTANCE = new Controller();
		}
		return INSTANCE;
	}

	public void changeState(ParamsHashMap hmap) {
		String category = hmap.get(Constants.PARAM_CATEGORY);
		Category c = Category.parse(category);

		MainUi.INSTANCE.selectCategory(c == null ? Category.PERSONAL : c);
	}
}
