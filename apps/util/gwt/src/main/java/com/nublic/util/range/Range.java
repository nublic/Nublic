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
	
	public boolean contains(int position) {
		return (position >= from) && (position <= to); 
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
		return to - from +1;
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
					rInList.setFrom(r.to + 1);
				} else {
					// |  [  |  ]
					rInList.setTo(r.from - 1);
				}
			}
		}
		from.removeAll(removeList);
		from.addAll(addList);
	}

	public static boolean contains(List<Range> rangesList, int position) {
		boolean contains = false;
		for (int i = 0; i < rangesList.size() && !contains; i++) {
			Range r = rangesList.get(i);
			contains = r.contains(position);
		}
		return contains;
	}
	
	public static void removeIntAndShift(List<Range> rangeList, int element) {
		List<Range> rangesToRemove = new ArrayList<Range>();
		for (Range r : rangeList) {
			if (element == r.to && element == r.from) {
				rangesToRemove.add(r);
			}

			if (element >= r.from && element <= r.to) {
				r.to--;
			} else if (element < r.from) {
				r.from--;
				r.to--;
			}
		}
		for (Range removeMe : rangesToRemove) {
			rangeList.remove(removeMe);
		}
	}
}
