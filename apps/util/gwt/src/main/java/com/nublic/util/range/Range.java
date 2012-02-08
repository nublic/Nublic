package com.nublic.util.range;

import java.util.ArrayList;
import java.util.List;

public class Range {
	int from;
	int to;

	public Range(int from, int to) {
		set(from, to);
	}
	
	// Getters and Setters
	public int getFrom() { return from; }
	public void setFrom(int from) {
		if (from <= to) {
			this.from = from;
		} else {
			throw new IllegalArgumentException();
		}
	}
	public int getTo() { return to; }
	public void setTo(int to) {
		if (to >= from) {
			this.to = to;
		} else {
			throw new IllegalArgumentException();
		}
	}
	public void set(int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException();
		}

		this.from = from;
		this.to = to;
	}

	public boolean intersects(Range r) {
		return (from <= r.to) && (to >= r.from);
	}
	
	public boolean contains(Range r) {
		return from <= r.from && to >= r.to; 
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Range)) {
			return false;
		} else {
			return (from == ((Range)o).from) && (to == ((Range)o).to);
		}
	}
	
	public int distance() {
		return to - from;
	}
	
	public static void remove(List<Range> from, Range r) {
		List<Range> removeList = new ArrayList<Range>();
		List<Range> addList = new ArrayList<Range>();
		for (Range rInList : from) {
			// r :  [   ]
			// rInList: |   |
			if (r.contains(rInList)) {
				// [ |   | ]
				removeList.add(rInList);
			} else if (rInList.contains(r)) {
				// |  [  ]  |
				removeList.add(rInList);
				if (rInList.from != r.from) {
					addList.add(new Range(rInList.from, r.from - 1));
				}
				if (r.to != rInList.to) {
					addList.add(new Range(r.to + 1, rInList.to));
				}
			} else if (r.intersects(rInList)) {
				if (r.from < rInList.from) {
					// [  |  ]  |
					rInList.setFrom(r.to);
				} else {
					// |  [  |  ]
					rInList.setTo(r.from);
				}
			}
		}
		from.removeAll(removeList);
		from.addAll(addList);
	}
}
