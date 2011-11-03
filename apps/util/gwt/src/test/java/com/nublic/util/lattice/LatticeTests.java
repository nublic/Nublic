package com.nublic.util.lattice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGeneratorSamples;
import net.java.quickcheck.generator.CombinedGenerators;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

public class LatticeTests {
	
	int NO_ELEMENTS = 10;
	LatticeElement[] elements;
	
	int NO_PERMUTATIONS = 500;
	List<Integer>[] permutations;
	
	SimpleLattice<LatticeElement> simpleL;
	GraphLattice<LatticeElement> graphL;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		// Generate lattice
		elements = new LatticeElement[NO_ELEMENTS];
		elements[0] = new LatticeElement(1, Lists.<Integer> newArrayList());
		elements[1] = new LatticeElement(2, Lists.newArrayList(1));
		elements[2] = new LatticeElement(3, Lists.newArrayList(1));
		elements[3] = new LatticeElement(4, Lists.newArrayList(1, 2, 3));
		elements[4] = new LatticeElement(5, Lists.newArrayList(1, 2, 3));
		elements[5] = new LatticeElement(6, Lists.newArrayList(1, 2, 3, 5));
		elements[6] = new LatticeElement(7, Lists.newArrayList(1, 2, 3, 5));
		elements[7] = new LatticeElement(8, Lists.<Integer> newArrayList());
		elements[8] = new LatticeElement(9, Lists.newArrayList(8));
		elements[9] = new LatticeElement(10, Lists.newArrayList(8, 9));
		// Generate permutations
		permutations = (List<Integer>[]) new List[NO_PERMUTATIONS];
		for(int k = 0; k < 500; k++) {
			permutations[k] = CombinedGeneratorSamples.anyList(permutationGenerator(0, NO_ELEMENTS - 1));
		}
		// Generate graphs
		simpleL = new SimpleLattice<LatticeElement>(new LatticeElementComparator());
		graphL = new GraphLattice<LatticeElement>(new LatticeElementComparator());
		for (LatticeElement e : elements) {
			simpleL.insert(e);
			graphL.insert(e);
		}
	}

	@Test
	public void testWithRespectToSpec() {
		// Test
		for(int k = 0; k < 500; k++) {
			List<Integer> permutation = permutations[k];
			// Create lattices
			SimpleLattice<LatticeElement> simple = new SimpleLattice<LatticeElement>(new LatticeElementComparator());
			GraphLattice<LatticeElement> graph = new GraphLattice<LatticeElement>(new LatticeElementComparator());
			// Add elements in permutation
			for (int i = 0; i < permutation.size(); i++) {
				LatticeElement elementToInsert = elements[permutation.get(i)];
				simple.insert(elementToInsert);
				graph.insert(elementToInsert);
				// Check it is inserted
				assertTrue(simple.contains(elementToInsert));
				assertTrue(graph.contains(elementToInsert));
				// Check in each stage everything is fine
				for (int j = 0; j <= i; j++) {
					LatticeElement elementToCheck = elements[permutation.get(j)];
					assertEquals(Sets.newHashSet(simple.elementsGreaterThan(elementToCheck)),
							Sets.newHashSet(graph.elementsGreaterThan(elementToCheck)));
					assertEquals(Sets.newHashSet(simple.elementsLessThan(elementToCheck)),
							Sets.newHashSet(graph.elementsLessThan(elementToCheck)));
				}
			}
			// Remove elements in permutation
			for (int i = 0; i < permutation.size(); i++) {
				LatticeElement elementToRemove = elements[permutation.get(i)];
				simple.remove(elementToRemove);
				graph.remove(elementToRemove);
				// Check it is removed
				assertFalse(simple.contains(elementToRemove));
				assertFalse(graph.contains(elementToRemove));
				// Check in each stage everything is fine
				for (int j = i + 1; j < permutation.size(); j++) {
					LatticeElement elementToCheck = elements[permutation.get(j)];
					assertEquals(Sets.newHashSet(simple.elementsGreaterThan(elementToCheck)),
							Sets.newHashSet(graph.elementsGreaterThan(elementToCheck)));
					assertEquals(Sets.newHashSet(simple.elementsLessThan(elementToCheck)),
							Sets.newHashSet(graph.elementsLessThan(elementToCheck)));
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
	
	@Rule
    public ContiPerfRule i = new ContiPerfRule();
	
	@Test
    @PerfTest(invocations = 500, threads = 50)
	public void testPerformanceOnSimple() {
		for(int k = 0; k < 500; k++) {
			List<Integer> permutation = permutations[k];
			SimpleLattice<LatticeElement> simple = new SimpleLattice<LatticeElement>(new LatticeElementComparator());
			testPerformanceOn(simple, permutation);
		}
	}
	
	@Test
    @PerfTest(invocations = 500, threads = 50)
	public void testPerformanceOnGraph() {
		for(int k = 0; k < 500; k++) {
			List<Integer> permutation = permutations[k];
			GraphLattice<LatticeElement> simple = new GraphLattice<LatticeElement>(new LatticeElementComparator());
			testPerformanceOn(simple, permutation);
		}
	}
	
	public void testPerformanceOn(Lattice<LatticeElement> l, List<Integer> permutation) {
		// Add elements in permutation
		for (int i = 0; i < permutation.size(); i++) {
			LatticeElement elementToInsert = elements[permutation.get(i)];
			l.insert(elementToInsert);
			// Check it is inserted
			l.contains(elementToInsert);
			// Check in each stage everything is fine
			for (int j = 0; j <= i; j++) {
				LatticeElement elementToCheck = elements[permutation.get(j)];
				l.elementsGreaterThan(elementToCheck);
				l.elementsLessThan(elementToCheck);
			}
		}
		// Remove elements in permutation
		for (int i = 0; i < permutation.size(); i++) {
			LatticeElement elementToRemove = elements[permutation.get(i)];
			l.remove(elementToRemove);
			// Check it is removed
			l.contains(elementToRemove);
			// Check in each stage everything is fine
			for (int j = i + 1; j < permutation.size(); j++) {
				LatticeElement elementToCheck = elements[permutation.get(j)];
				l.elementsGreaterThan(elementToCheck);
				l.elementsLessThan(elementToCheck);
			}
		}
	}
	
	@Test
    @PerfTest(invocations = 10000, threads = 50)
	public void testSearchOnSimple() {
		testSearchOn(simpleL);
	}
	
	@Test
    @PerfTest(invocations = 10000, threads = 50)
	public void testSearchOnGraph() {
		testSearchOn(graphL);
	}
	
	public void testSearchOn(Lattice<LatticeElement> l) {
		for (LatticeElement e : elements) {
			l.elementsGreaterThan(e);
			l.elementsLessThan(e);
		}
	}
	
}
