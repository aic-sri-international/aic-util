package com.sri.ai.util.explanation.logging.core;

import com.sri.ai.util.explanation.logging.api.ExplanationRecord;

public class DefaultExplanationRecord implements ExplanationRecord {
	
	final double importance;				// raw marked importance (relative to block it record was extracted from)
	final double compundedImportance;		// overall importance (importance values having been multiplied when entering each successive explanation block)
	final int nestingDepth;					// depth of nesting record was extracted from within nested start/end explanation blocks
	final Object[] objects;					// objects that were recorded from the explanation
	
	public DefaultExplanationRecord(double importance, double compoundedImportance, int nestingDepth, Object[] objects) {
		this.importance = importance;
		this.compundedImportance = compoundedImportance;
		this.nestingDepth = nestingDepth;
		this.objects = objects;
	}

	@Override
	public Number getImportance() {
		return importance;
	}

	@Override
	public Number getCompundedImportance() {
		return compundedImportance;
	}

	@Override
	public int getNestingDepth() {
		return nestingDepth;
	}

	@Override
	public Object[] getObjects() {
		return objects;
	}

}
