package com.nublic.util.lattice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.nublic.util.graph.DefaultEdge;
import com.nublic.util.graph.DirectedGraph;
import com.nublic.util.graph.DirectedSubgraph;
import com.nublic.util.graph.EdgeReversedGraph;
import com.nublic.util.graph.Graph;
import com.nublic.util.graph.SimpleDirectedGraph;
import com.nublic.util.graph.algorithms.BellmanFordShortestPath;
import com.nublic.util.graph.traversal.DepthFirstIterator;

public class GraphLattice<T> implements Lattice<T> {
	
	PartialComparator<T> comparator;
	SimpleDirectedGraph<T, DefaultEdge> graph;
	
	public GraphLattice(PartialComparator<T> comparator) {
		this.comparator = comparator;
		this.graph = new SimpleDirectedGraph<T, DefaultEdge>(DefaultEdge.class);
	}

	@Override
	public void insert(final T t) {
		// Generate maximal and minimal sets
		final Set<T> greater = Sets.filter(graph.vertexSet(), new Predicate<T>() {
			@Override
			public boolean apply(T e) {
				return comparator.compare(e, t) == Ordering.GREATER;
			}
		});
		final Set<T> less = Sets.filter(graph.vertexSet(), new Predicate<T>() {
			@Override
			public boolean apply(T e) {
				return comparator.compare(e, t) == Ordering.LESS;
			}
		});
		Set<T> greater_minimal = Sets.filter(greater, new Predicate<T>() {
			@Override
			public boolean apply(T e) {
				for (T g : greater) {
					if (comparator.compare(e, g) == Ordering.GREATER) {
						return false;
					}
				}
				return true;
			}
		});
		Set<T> less_maximal = Sets.filter(less, new Predicate<T>() {
			@Override
			public boolean apply(T e) {
				for (T l : less) {
					if (comparator.compare(e, l) == Ordering.LESS) {
						return false;
					}
				}
				return true;
			}
		});
		// Check if some edge exists and break it
		for (T in :less_maximal) {
			for (T out : greater_minimal) {
				if (graph.containsEdge(in, out)) {
					graph.removeEdge(in, out);
				}
			}
		}
		// Add vertex
		graph.addVertex(t);
		// Create edges
		for (T in : less_maximal) {
			graph.addEdge(in, t);
		}
		for (T out : greater_minimal) {
			graph.addEdge(t, out);
		}
	}

	@Override
	public void remove(T t) {
		// Check the vertex is there
		if (!graph.containsVertex(t))
			return;
		
		Collection<T> in = Collections2.transform(graph.incomingEdgesOf(t), new Function<DefaultEdge, T>() {
			@Override
			public T apply(DefaultEdge e) {
				return graph.getEdgeSource(e);
			}
		});
		Collection<T> out = Collections2.transform(graph.outgoingEdgesOf(t), new Function<DefaultEdge, T>() {
			@Override
			public T apply(DefaultEdge e) {
				return graph.getEdgeTarget(e);
			}
		});
		graph.removeVertex(t);
		for (T inEdge : in) {
			BellmanFordShortestPath<T, DefaultEdge> bellman =
					new BellmanFordShortestPath<T, DefaultEdge>(graph, inEdge);
			ArrayList<T> withoutPath = Lists.newArrayList();
			for (T outEdge : out) {
				if (Double.isInfinite(bellman.getCost(outEdge))) {
					withoutPath.add(outEdge);
				}
			}
			for (T outEdge : withoutPath) {
				graph.addEdge(inEdge, outEdge);
			}
		}
	}
	
	@Override
	public boolean contains(T t) {
		return graph.containsVertex(t);
	}

	@Override
	public List<T> elementsGreaterThan(T t) {
		return elementsReachable(graph, t);
	}

	@Override
	public List<T> elementsLessThan(T t) {
		return elementsReachable(new EdgeReversedGraph<T, DefaultEdge>(graph), t);
	}
	
	public DirectedSubgraph<T, DefaultEdge> subgraphGreaterThan(T t) {
		return subgraphReachable(graph, t);
	}

	public DirectedSubgraph<T, DefaultEdge> subgraphLessThan(T t) {
		return subgraphReachable(new EdgeReversedGraph<T, DefaultEdge>(graph), t);
	}
	
	private List<T> elementsReachable(Graph<T, DefaultEdge> g, T t) {
		DepthFirstIterator<T, DefaultEdge> depth = new DepthFirstIterator<T, DefaultEdge>(g, t);
		List<T> items = Lists.newArrayList(depth);
		items.remove(0);
		return items;
	}
	
	private DirectedSubgraph<T, DefaultEdge> subgraphReachable(DirectedGraph<T, DefaultEdge> g, T t) {
		DepthFirstIterator<T, DefaultEdge> depth = new DepthFirstIterator<T, DefaultEdge>(g, t);
		Set<T> items = Sets.newHashSet(depth);
		return new DirectedSubgraph<T, DefaultEdge>(g, items, null);
	}

}
