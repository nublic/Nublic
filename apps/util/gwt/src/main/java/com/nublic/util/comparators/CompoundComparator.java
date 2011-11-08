package com.nublic.util.comparators;

import java.util.Comparator;

public class CompoundComparator<T> implements Comparator <T> {
	Comparator<? super T> c1;
	Comparator<? super T> c2;

	public CompoundComparator(Comparator<? super T> c1, Comparator<? super T> c2) {
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public static <R> CompoundComparator<R> create(Comparator<? super R>... comps) {
		if (comps.length < 2) {
			throw new IllegalArgumentException();
		} else if (comps.length == 2) {
			return new CompoundComparator<R>(comps[0], comps[1]);
		} else {
//			Comparator<? super R>[] comps_bis = Arrays.<Comparator<? super R>>copyOfRange(comps, 1, comps.length - 1);
			@SuppressWarnings("unchecked")
			Comparator<? super R>[] comps_bis = (Comparator<? super R>[]) new Comparator[comps.length - 1]; 
			for (int i = 1; i < comps.length ; i ++) {
				comps_bis[i - 1] = comps[i];
			}
			return new CompoundComparator<R>(comps[0], create(comps_bis));
		}
	}

	@Override
	public int compare(T o1, T o2) {
		int res = c1.compare(o1, o2);
		return res == 0 ? c2.compare(o1, o2) : res;
	}

}
