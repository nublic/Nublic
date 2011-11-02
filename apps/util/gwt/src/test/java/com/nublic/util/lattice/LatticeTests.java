package com.nublic.util.lattice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGenerators;
import net.java.quickcheck.generator.CombinedGeneratorsIterables;

import org.junit.Test;

import com.google.gwt.thirdparty.guava.common.collect.Lists;

public class LatticeTests {

	@Test
	public void testWithRespectToSpec() {
		int lo = 0, hi = 9;
		// Create elements
		LatticeElement[] elements = new LatticeElement[10];
		elements[0] = new LatticeElement(1, Lists.<Integer>newArrayList());
		elements[1] = new LatticeElement(2, Lists.newArrayList(1));
		elements[2] = new LatticeElement(3, Lists.newArrayList(1));
		elements[3] = new LatticeElement(4, Lists.newArrayList(1, 2, 3));
		elements[4] = new LatticeElement(5, Lists.newArrayList(1, 2, 3));
		elements[5] = new LatticeElement(6, Lists.newArrayList(1, 2, 3, 5));
		elements[6] = new LatticeElement(7, Lists.newArrayList(1, 2, 3, 5));
		elements[7] = new LatticeElement(8, Lists.<Integer>newArrayList());
		elements[8] = new LatticeElement(9, Lists.newArrayList(8));
		elements[9] = new LatticeElement(10, Lists.newArrayList(8, 9));
		// Test
		for(List<Integer> permutation : 
			CombinedGeneratorsIterables.someNonEmptyLists(permutationGenerator(lo, hi))) {
			// Create lattices
			SimpleLattice<LatticeElement> simple = new SimpleLattice<LatticeElement>(new LatticeElementComparator());
			GraphLattice<LatticeElement> graph = new GraphLattice<LatticeElement>(new LatticeElementComparator());
			// Add elements in permutation
			for (int i = 0; i < permutation.size(); i++) {
				LatticeElement elementToInsert = elements[permutation.get(i)];
				simple.insert(elementToInsert);
				graph.insert(elementToInsert);
				// Check in each stage everything is fine
				for (int j = 0; j <= i; j++) {
					LatticeElement elementToCheck = elements[permutation.get(j)];
					assertEquals(simple.elementsGreaterThan(elementToCheck),
							graph.elementsGreaterThan(elementToCheck));
					assertEquals(simple.elementsLessThan(elementToCheck),
							graph.elementsLessThan(elementToCheck));
				}
			}
			// Remove elements in permutation
			for (int i = 0; i < permutation.size(); i++) {
				LatticeElement elementToRemove = elements[permutation.get(i)];
				simple.remove(elementToRemove);
				graph.remove(elementToRemove);
				// Check in each stage everything is fine
				for (int j = i + 1; j < permutation.size(); j++) {
					LatticeElement elementToCheck = elements[permutation.get(j)];
					assertEquals(simple.elementsGreaterThan(elementToCheck),
							graph.elementsGreaterThan(elementToCheck));
					assertEquals(simple.elementsLessThan(elementToCheck),
							graph.elementsLessThan(elementToCheck));
				}
			}
		}
	}
	
	public Generator<Integer> permutationGenerator(int lo, int hi) {
		ArrayList<Integer> numbers = Lists.newArrayList();
		for (int i = lo; i <= hi; i++) {
			numbers.add(i);
		}
		return CombinedGenerators.ensureValues(numbers);
	}

}
