package com.sri.ai.util.explanation.logging.api;

public interface ExplanationRecord {
	
	public Number getImportance();
	
	public Number getCompundedImportance();

	public int getNestingDepth();

	public Object[] getObjects();

}
