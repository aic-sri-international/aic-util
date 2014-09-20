/*
 * Copyright (c) 2013, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.util.collect;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;

/**
 * A facility for creating a depth-first iterator over the subtrees of a
 * user-defined tree (by default including the root subtree itself, which is the
 * same as the original tree).
 * <p>
 * The user defines the tree by providing a root object and a method
 * {@link #getChildrenIterator(Object)} returning an iterator for the children
 * of a given object (or <code>null</code> if there are no children).
 * <p>
 * Let C be the class extending {@link DepthFirstIterator}. Child nodes are
 * explored depth-first by constructing an instance of C for each of them. This
 * instance is constructed by using Java's reflection mechanism to find a
 * single-parameter constructor. If however C does not have a single-parameter
 * constructor, it needs to override
 * {@link #makeDepthFirstIteratorOfCurrentExtendingClass(Object child)} to
 * provide a depth-first iterator on a given child. Typically
 * {@link #makeDepthFirstIteratorOfCurrentExtendingClass(Object child)} will do
 * this by using a C constructor taking more than one parameter, but any
 * depth-first iterator based on the child will do (in fact, any iterator of
 * child, even if not depth-first, would work, but this would render the overall
 * iterator depth-first on the first level only).
 * <p>
 * The walk can be pruned by overriding the method {@link #pruneChildren()},
 * which must return <code>true</code> if the <i>children</i> of {@link #root}
 * must not be traversed. Note that each visited node has a corresponding
 * DepthFirstIterator, so it will be its root. Therefore, {@link #pruneChildren()} will be
 * applied to each visited node.
 * <p>
 * Also note that, if prune returns <code>true</code> for a certain node, that
 * node is still visited, but not its children. This may contradict an intuition
 * that the node itself should not be visited. The reason for this design
 * decision is that, should we wish not to visit a node based on its parent's
 * properties, it would be difficult to write a prune method, which does not
 * have access to a node's parent node. The way prune works, one can easily do
 * that by using a node's property to avoid visiting its children. If we want to
 * avoid visiting the node itself, we can use {@link PredicateIterator} to
 * filter them out as well.
 * <p>
 * By default, {@link #pruneChildren()} returns the result of evaluating
 * {@link #pruneChildrenPredicate} on {@link #root} if that field is not
 * <code>null</code>, and <code>false</code> otherwise. Therefore, pruning
 * functionality can be implemented either by overriding
 * {@link #pruneChildren()} in an extending class, or assigning a new
 * {@link Predicate} to {@link #pruneChildrenPredicate} (using its setter
 * {@link #setPruneChildrenPredicate(Predicate)}).
 * 
 * @author braz
 */
@Beta
public abstract class DepthFirstIterator<E> extends EZIterator<E> {
	protected E root;
	private boolean hasToReturnRootStill;
	private Iterator<E> childrenIterator;
	private Iterator<E> currentChildIterator;
	protected Predicate<E> pruneChildrenPredicate = null;
	
	public DepthFirstIterator(E root, boolean mustReturnRootAsWell) {
		setUp(root);
		hasToReturnRootStill = mustReturnRootAsWell;
	}

	public DepthFirstIterator(E root) {
		this(root, true);
	}

	public abstract Iterator<E> getChildrenIterator(E object);
	
	public abstract DepthFirstIterator<E> newInstance(E object);

	/**
	 * Given an object, makes a DepthFirstIterator (an instance of the invoking
	 * extension, rather than DepthFirstIterator proper) with that object as
	 * root.
	 * 
	 * @param object
	 *            the root object for the iterator.
	 * @return a new DepthFirstIterator with the given object as root.
	 * @throws InstantiationException
	 *         an instantiation exception.
	 * @throws IllegalAccessException
	 *         an illegal access exception.
	 * @throws InvocationTargetException
	 *         an invocation target exception.
	 */
	protected DepthFirstIterator<E> makeDepthFirstIteratorOfCurrentExtendingClass(E object)
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException {
		DepthFirstIterator<E> result  = newInstance(object);
		result.pruneChildrenPredicate = pruneChildrenPredicate;
		return result;
	}

	private void setUp(E root) {
		this.root             = root;
		this.childrenIterator = null;
	}

	@Override
	protected E calculateNext() {
		if (hasToReturnRootStill) {
			hasToReturnRootStill = false;
			return root;
		}

		// pruneChildren() decides whether children must be visited or not
		if (childrenIterator == null && !pruneChildren()) { 
			childrenIterator = getChildrenIterator(root);
		}
		
		if (childrenIterator == null) {
			return null;
		}
	
		ensureCurrentChildIteratorHasNextOrIsNull();

		if (currentChildIterator != null) {
			return currentChildIterator.next();
		}

		return null;
	}

	private void ensureCurrentChildIteratorHasNextOrIsNull() {
		while (currentChildIterator == null || !currentChildIterator.hasNext()) {
			if ( ! childrenIterator.hasNext()) {
				currentChildIterator = null;
				return;
			}
			else {
				E child = childrenIterator.next();
				try {
					currentChildIterator = makeDepthFirstIteratorOfCurrentExtendingClass(child);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean pruneChildren() {
		if (pruneChildrenPredicate == null) {
			return false;
		}
		else {
			boolean result = pruneChildrenPredicate.apply(root);
			return result;
		}
	}

	/**
	 * @return the current prune children predicate, used by the default
	 *         implementation of {@link #pruneChildren()} to decide whether to
	 *         prune the children of the current node (that default
	 *         implementation returns <code>false</code> if the prune predicate
	 *         is <code>null</code>).
	 */
	public Predicate getPruneChildrenPredicate() {
		return pruneChildrenPredicate;
	}

	/**
	 * Sets the prune children predicate, used by the default implementation of
	 * {@link #pruneChildren()} to decide whether to prune the children of the
	 * current node (that default implementation returns <code>false</code> if
	 * the prune predicate is <code>null</code>).
	 * 
	 * @param prunePredicate
	 *        the prune predicate to set.
	 */
	public void setPruneChildrenPredicate(Predicate<E> prunePredicate) {
		this.pruneChildrenPredicate = prunePredicate;
	}
}
