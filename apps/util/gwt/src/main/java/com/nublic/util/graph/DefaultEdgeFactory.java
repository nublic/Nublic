package com.nublic.util.graph;

public class DefaultEdgeFactory<V> implements EdgeFactory<V, DefaultEdge> {

	public DefaultEdge createEdge(V source, V target)
    {
        try {
        	return new DefaultEdge();
        } catch (Exception ex) {
            throw new RuntimeException("Edge factory failed", ex);
        }
    }
}
