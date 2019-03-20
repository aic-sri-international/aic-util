package com.sri.ai.util.tree;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.mapIntoList;

import java.util.Collection;
import java.util.Iterator;

public class DefaultTree<T> implements Tree<T> {
	
	private T information;
	private Collection<? extends Tree<? extends T>> children;
	
	/**
	 * Creates tree with given information and no children.
	 * @param information
	 * @return
	 */
	public static <T1> DefaultTree<T1> tree(T1 information) {
		return new DefaultTree<T1>(information, list());
	}
	
	/**
	 * Creates three with given information and children
	 * @param information
	 * @param children
	 * @return
	 */
	public static <T1> DefaultTree<T1> tree(T1 information, Collection<? extends Tree<? extends T1>> children) {
		return new DefaultTree<T1>(information, children);
	}
	
	/**
	 * Creates a one-level tree with given information and children with information items given in collection.
	 * @param information
	 * @param childrenInformation
	 * @return
	 */
	public static <T1> DefaultTree<T1> treeOneLevel(T1 information, Collection<? extends T1> childrenInformation) {
		return new DefaultTree<T1>(information, mapIntoList(childrenInformation, i -> tree(i)));
	}
	
	public DefaultTree(T information) {
		this(information, list());
	}

	public DefaultTree(T information, Collection<? extends Tree<? extends T>> children) {
		this.information = information;
		this.children = children;
	}

	@Override
	public T getInformation() {
		return information;
	}

	@Override
	public Iterator<? extends Tree<? extends T>> getChildren() {
		return children.iterator();
	}

}
