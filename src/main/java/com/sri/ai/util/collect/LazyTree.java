package com.sri.ai.util.collect;

import java.util.Iterator;

import com.sri.ai.util.base.NullaryFunction;

/**
 * The interface for the nodes of lazily created trees.
 * A node is associated with information of type <code>E</code> and
 * an iterator for thunks ({@link NullaryFunction});
 * each of these thunks must return a subtree of the node.
 * 
 * @author braz
 *
 * @param <E>
 */
interface LazyTree<E> {

	/**
	 * Returns this node's information
	 * @return this node's information
	 */
	E getInformation();
	
	/**
	 * An iterator of thunks creating this node's sub-trees.
	 * Leaves must return an empty iterator.
	 * @return An iterator of thunks creating this node's sub-trees
	 * (the iterator will have no next element if the node is a information).
	 */
	Iterator<NullaryFunction<LazyTree<E>>> getSubTreeMakers();
}