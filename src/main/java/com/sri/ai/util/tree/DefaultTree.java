package com.sri.ai.util.tree;

import static com.sri.ai.util.Util.list;

import java.util.Collection;
import java.util.Iterator;

public class DefaultTree<T> implements Tree<T> {
	
	private T information;
	private Collection<? extends Tree<? extends T>> children;
	
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
