package com.nublic.util.lattice;

public enum Ordering {
	EQUAL,
	GREATER,
	LESS,
	INCOMPARABLE;
	
	Ordering inverse() {
		switch(this) {
		case GREATER:
			return LESS;
		case LESS:
			return GREATER;
		default:
			return this;
		}
	}
}
