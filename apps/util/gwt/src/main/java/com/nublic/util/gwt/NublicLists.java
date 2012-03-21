package com.nublic.util.gwt;

import java.util.Collection;

public class NublicLists {
	// toString method of E will be used
	public static <E> String joinList(Collection<E> list, String separator) {
		StringBuilder joinedList = new StringBuilder();
		boolean first = true;
		for (E e : list) {
			if (!first) {
				joinedList.append(separator);
			} else {
				first = false;
			}
			joinedList.append(e.toString());
		}
		return joinedList.toString();
	}

}
