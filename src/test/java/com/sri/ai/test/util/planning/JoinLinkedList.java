package com.sri.ai.test.util.planning;

import static com.sri.ai.util.Util.join;

import java.util.Collection;
import java.util.LinkedList;

public class JoinLinkedList<T> extends LinkedList<T> {

	private static final long serialVersionUID = 1L;

	private String separator;
	
	@SuppressWarnings("unchecked")
	public JoinLinkedList(String separator, Collection<? extends Object> elements) {
		elements.forEach(e -> add((T) e));
		this.separator = separator;
	}
	
	String getSeparator() {
		return separator;
	}
	
	@Override
	public String toString() {
		return join(separator, this);
	}
}
