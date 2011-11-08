package com.nublic.util.comparators;

import java.util.Comparator;

public class CompoundComparator<T> implements Comparator <T> {
	Comparator<? super T> c1;
	Comparator<? super T> c2;

	CompoundComparator(Comparator<? super T> c1, Comparator<? super T> c2) {
		this.c1 = c1;
		this.c2 = c2;
	}

	@Override
	public int compare(T o1, T o2) {
		int res = c1.compare(o1, o2);
		return res == 0 ? c2.compare(o1, o2) : res;
//		if (res == 0) {
//			return c2.compare(o1, o2);
//		} else {
//			return res;
//		}
	}

}
