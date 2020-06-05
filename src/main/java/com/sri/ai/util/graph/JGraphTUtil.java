package com.sri.ai.util.graph;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

/**
 * Some convenience methods for using JGraphT.
 * 
 * @author braz
 *
 */
public class JGraphTUtil {
	
	/**
	 * Given an iterable of vertices,
	 * and an <code>haveEdge</code> binary predicate (assumed to only be true for distinct vertices), 
	 * returns a {@link Graph}.
	 * @param vertices
	 * @param haveEdge
	 * @return
	 */
	public static <V> Graph<V, DefaultEdge> makeGraph(Iterable<? extends V> vertices, BiPredicate<? super V, ? super V> edge) {
		Graph<V, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
		for (V v1 : vertices) {
			graph.addVertex(v1);
			for (V v2 : vertices) {
				graph.addVertex(v2);
				if (edge.test(v1, v2)) {
					graph.addEdge(v1, v2);
				}
			}
		}
		return graph;
	}

	/** Returns the sets of connected vertices in a graph. */
	public static <V> List<? extends Set<? extends V>> getConnectedSets(Graph<V, DefaultEdge> graph) {
		return new ConnectivityInspector<>(graph).connectedSets();
	}

	/**
	 * Returns the connected sets of vertices of a graph built accordingly to makeGraph.
	 * @param vertices
	 * @param haveEdge
	 * @return
	 */
	public static <V> List<? extends Set<? extends V>> getConnectedSets(Iterable<? extends V> vertices, BiPredicate<? super V, ? super V> haveEdge) {
		return getConnectedSets(makeGraph(vertices, haveEdge));	
	}

}
