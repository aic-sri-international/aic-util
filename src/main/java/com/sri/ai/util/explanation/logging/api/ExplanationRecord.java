package com.sri.ai.util.explanation.logging.api;

import com.sri.ai.util.base.Wrapper;

public interface ExplanationRecord {
	
	Number getImportance();
	
	Number getAdjustedImportance();

	int getNestingDepth();

	long getTimestamp();

	Object[] getObjects();

	long getBlockTime();
	
	long getRecordId();
	
	static Wrapper<Long> counter = new Wrapper<>(0L);

}
