package com.nublic.app.manager.settings.client;

public enum Category {
	PERSONAL(Constants.VALUE_PERSONAL),
	WORK_FOLDERS(Constants.VALUE_WORK_FOLDERS),
	PRIVACY(Constants.VALUE_PRIVACY),
	SYSTEM(Constants.VALUE_SYSTEM),
	USERS(Constants.VALUE_USERS);
	
	private String str;
	
	private Category(String s) {
		str = s;
	}
	
	@Override
	public String toString() {
		return str;
	}

	public static Category parse(String catString) {
		if (catString == null) {
			return null;
		}
		for (Category c : Category.values()) {
			if (c.toString().compareTo(catString) == 0) {
				return c;
			}
		}
		return null;
	}
}
