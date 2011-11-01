package com.nublic.util.lattice;

public enum Ordering {
	EQUAL,
	GREATER,
	LESSER,
	INCOMPARABLE;
	
	Ordering inverse() {
		switch(this) {
		case GREATER:
			return LESSER;
		case LESSER:
			return GREATER;
		default:
			return this;
		}
	}
}
