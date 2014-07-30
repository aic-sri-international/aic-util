package com.sri.ai.util.rangeoperation;

import java.util.Collection;

/** A range over a discrete set of values. */
public class DiscreteRange extends AbstractRange {
	public DiscreteRange(String name, Collection collection) {
		super(name);
		this.collection = collection;
	}
	@Override
	public Object apply() { 
		return collection.iterator();
	}	
	private Collection collection;
}