package com.sri.ai.util.explanation.logging.api;

public interface ExplanationRecord {
	
	public Number getImportance();
	
	public Number getAdjustedImportance();

	public int getNestingDepth();

	public long getTimestamp();

	public Object[] getObjects();

	public long getBlockTime();

}
