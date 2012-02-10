package com.nublic.util.range;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RangeTest {

	@Test
	public void test() {
		Range from1To100 = new Range(1,100);
		Range from1To10 = new Range(1,10);
		Range from10To90 = new Range(10,90);
		Range from11To20 = new Range(11,20);
		Range from90To100 = new Range(90,100);

		// Not destructive functions
		assertTrue(from1To100.distance() == 100);

		assertFalse(from1To10.intersects(from11To20));
		assertFalse(from11To20.intersects(from1To10));
		assertTrue(from1To100.intersects(from1To10));
		assertTrue(from1To100.intersects(from90To100));
		assertTrue(from1To100.intersects(from10To90));
		
		assertTrue(from1To100.contains(from10To90));
		assertTrue(from1To100.contains(from1To10));
		assertTrue(from1To100.contains(from90To100));

		// Destructive functions
		List<Range> rangeList = new ArrayList<Range>();
		rangeList.add(from1To100);
		assertTrue(Range.contains(rangeList, 100));
		assertTrue(Range.contains(rangeList, 1));

		Range.remove(rangeList, from11To20);
		assertTrue(rangeList.size() == 2);
		assertTrue(Range.contains(rangeList, 10));
		assertTrue(Range.contains(rangeList, 21));
		assertTrue(Range.contains(rangeList, 58));
		assertTrue(Range.contains(rangeList, 100));
		assertTrue(Range.contains(rangeList, 1));
		assertFalse(Range.contains(rangeList, 0));
		assertFalse(Range.contains(rangeList, 11));
		assertFalse(Range.contains(rangeList, 20));
		assertFalse(Range.contains(rangeList, 15));
		
		Range.remove(rangeList, from1To10);
		assertTrue(rangeList.size() == 1);
		assertTrue(Range.contains(rangeList, 21));
		assertFalse(Range.contains(rangeList, 1));
		assertFalse(Range.contains(rangeList, 10));
		assertFalse(Range.contains(rangeList, 5));

		Range.remove(rangeList, from10To90);
		assertTrue(rangeList.size() == 1);
		assertTrue(rangeList.get(0).distance() == 10);
		assertFalse(Range.contains(rangeList, 53));
		assertFalse(Range.contains(rangeList, 10));
		assertFalse(Range.contains(rangeList, 21));
		assertFalse(Range.contains(rangeList, 90));
		assertTrue(Range.contains(rangeList, 96));
		assertTrue(Range.contains(rangeList, 91));

		Range from92to110 = new Range(92,100);
		Range.remove(rangeList, from92to110);
		assertTrue(rangeList.size() == 1);
		assertTrue(rangeList.get(0).distance() == 1);
		assertTrue(Range.contains(rangeList, 91));
		assertFalse(Range.contains(rangeList, 100));
		assertFalse(Range.contains(rangeList, 92));

		Range from50to150 = new Range(50,150);
		Range.remove(rangeList, from50to150);
		assertTrue(rangeList.isEmpty());
		
	}

}
