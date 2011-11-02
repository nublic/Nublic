package com.nublic.util.messages;

public interface LatticeComparable<T> {
	final static int EQ = 0;
	final static int GT = 1;
	final static int LT = 2;
	final static int NOT_COMPARABLE = 3;
	
	int compareTo(T elem);
}
