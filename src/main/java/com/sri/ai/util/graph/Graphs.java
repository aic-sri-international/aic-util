package com.sri.ai.util.graph;

import static com.sri.ai.util.Util.in;
import static com.sri.ai.util.Util.set;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

public class Graphs {
	
	/**
	 * Given a graph vertex and a supplier of iterators to a vertex's neighbors,
	 * returns the set of vertices in its component,
	 * that is, the set of vertices reachable from given vertex (including itself).
	 * @param vertex
	 * @param neighborsIteratorMaker
	 * @return
	 */
	public static <T> Set<T> component(T vertex, Function<T, Iterator<T>> neighborsIteratorMaker) {
		return component(vertex, neighborsIteratorMaker, set());
	}
	
	private static <T> Set<T> component(T vertex, Function<T, Iterator<T>> neighborsIteratorMaker, Set<T> result) {
		result.add(vertex);
		for (T neighbor : in(neighborsIteratorMaker.apply(vertex))) {
			if ( ! result.contains(neighbor)) {
				component(neighbor, neighborsIteratorMaker, result);
			}
		}
		return result;
	}
}
