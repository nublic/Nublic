package com.nublic.util.lattice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.nublic.util.graph.DefaultEdge;
import com.nublic.util.graph.EdgeReversedGraph;
import com.nublic.util.graph.Graph;
import com.nublic.util.graph.SimpleDirectedGraph;
import com.nublic.util.graph.algorithms.BellmanFordShortestPath;

public class GraphLattice<T> implements Lattice<T> {
	
	PartialComparator<T> comparator;
	SimpleDirectedGraph<T, DefaultEdge> graph;
	
	public GraphLattice(PartialComparator<T> comparator) {
		this.comparator = comparator;
		this.graph = new SimpleDirectedGraph<T, DefaultEdge>(DefaultEdge.class);
	}

	@Override
	public void insert(T t) {
		
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
	public Set<T> elementsGreaterThan(T t) {
		return elementsReachable(graph, t);
	}

	@Override
	public Set<T> elementsLessThan(T t) {
		return elementsReachable(new EdgeReversedGraph<T, DefaultEdge>(graph), t);
	}
	
	private Set<T> elementsReachable(Graph<T, DefaultEdge> g, T t) {
		final BellmanFordShortestPath<T, DefaultEdge> bellman = new BellmanFordShortestPath<T, DefaultEdge>(g, t);
		Set<T> vertices = Sets.newHashSet(graph.vertexSet());
		Collection<T> filtered = Collections2.filter(vertices, new Predicate<T>() {
			@Override
			public boolean apply(T e) {
				double cost = bellman.getCost(e);
				return cost > 0 && !Double.isInfinite(cost);
			}
		});
		return Sets.newHashSet(filtered);
	}

}
