package com.nublic.app.manager.web.frame;

public class Counter {
	public static long NOT_ALLOWED = 0;
	private long number = 1;
	
	public long next() {
		number++;
		if (number == NOT_ALLOWED)
			number++;
		return number;
	}
}
